package co.blastlab.serviceblbnavi.socket.info;

import co.blastlab.serviceblbnavi.dao.repository.DeviceRepository;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import co.blastlab.serviceblbnavi.service.DeviceService;
import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.info.client.UpdateRequest;
import co.blastlab.serviceblbnavi.socket.info.controller.DeviceStatus;
import co.blastlab.serviceblbnavi.socket.info.controller.Network;
import co.blastlab.serviceblbnavi.socket.info.controller.NetworkController;
import co.blastlab.serviceblbnavi.socket.info.helper.Crc16;
import co.blastlab.serviceblbnavi.socket.info.helper.JsonHelper;
import co.blastlab.serviceblbnavi.socket.info.server.Info;
import co.blastlab.serviceblbnavi.socket.info.server.Info.InfoType;
import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import co.blastlab.serviceblbnavi.socket.info.server.broadcast.DeviceConnected;
import co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo;
import co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo.FileInfoType;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.Deleted;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileAcknowledge;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListDetails;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListSummary;
import co.blastlab.serviceblbnavi.socket.info.server.file.out.AskList;
import co.blastlab.serviceblbnavi.socket.info.server.file.out.Delete;
import co.blastlab.serviceblbnavi.socket.info.server.file.out.Upload;
import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfo;
import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfo.UpdateInfoType;
import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfoCode;
import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfoCode.UpdateInfoCodeType;
import co.blastlab.serviceblbnavi.socket.info.server.update.in.UpdateAcknowledge;
import co.blastlab.serviceblbnavi.socket.info.server.update.out.Start;
import co.blastlab.serviceblbnavi.socket.info.server.version.Version;
import co.blastlab.serviceblbnavi.socket.wrappers.InfoErrorWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.InfoWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@ServerEndpoint("/info")
@Singleton
public class InfoWebSocket extends WebSocket {

	/*
		Error codes and explanation:
		IWS_001 - At least one of requested devices to update has not been found in the DB.
		IWS_002 - At least one of requested devices to update is not connected to the network.
		IWS_003 - At least one of requested anchors to update is not assigned to any sink.
		IWS_004 - Timeout exception has been thrown during send file.
		IWS_005 - Trying to free some space for new firmware, but somehow deletion failed.
		IWS_006 - File checksum did not validate properly.
		IWS_007 - Asking for file list failed.
		IWS_008 - At least one of requested tags is not connected to any sink.
		IWS_009 - The device didn't update due to abort on hardware side.
		IWS_010 - Other error that the user itself can not resolve.
		IWS_011 - The firmware is invalid.
	 */

	private static long TIMEOUT_SECONDS_ACK = 30;
	private static long AFTER_UPDATE_WAIT_TIME_SECONDS = 15;
	private static long OUTDATED_DEVICE_STATUS_MILISECONDS = 150000;

	@Inject
	private DeviceRepository deviceRepository;

	@Inject
	private DeviceService deviceService;

	@Inject
	private NetworkController networkController;

	@Resource
	private ManagedExecutorService managedExecutorService;

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());

	private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Override
	protected Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	protected Set<Session> getServerSessions() {
		return serverSessions;
	}

	@OnOpen
	public void open(Session session) {
		super.open(session, () -> {
			try {
				final Set<DeviceStatus> devices = new HashSet<>();
				if (session.getRequestParameterMap().containsKey("sinks")) {
					for (Network network : networkController.getNetworks()) {
						devices.add(network.getSink());
					}
				} else if (session.getRequestParameterMap().containsKey("anchors")) {
					for (Network network : networkController.getNetworks()) {
						devices.addAll(network.getAnchors());
					}
				} else if (session.getRequestParameterMap().containsKey("tags")) {
					for (Network network : networkController.getNetworks()) {
						devices.addAll(network.getTags());
					}
				}
				session.getBasicRemote().sendText(objectMapper.writeValueAsString(
					new InfoWrapper(devices))
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, () -> {});
	}

	@OnClose
	public void close(Session session) {
		super.close(session, () -> {}, () -> {
			networkController.unregister(session);
		});
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (isServerSession(session)) {
			List<Info> infos = objectMapper.readValue(message, new TypeReference<List<Info>>() {});
			for (Info info : infos) {
				InfoType infoType = InfoType.from(info.getCode());
				switch (infoType) {
					case STATION_WAKE_UP:
						break;
					case STATION_SLEEP:
						break;
					case VERSION:
						handleVersion(info);
						break;
					case BROADCAST:
						handleBroadcast(session, info);
						break;
					case STATUS:
						break;
					case FIRMWARE_UPDATE:
						handleFirmwareUpdate(session, info);
						break;
					case FILE:
						handleFileMessage(session, info);
						break;
				}
			}
		} else if (isClientSession(session)) {
			prepareUpload(message);
		}
	}

	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void updateStatuses() {
		networkController.getNetworks().forEach(network -> {
			for (DeviceStatus anchorStatus : network.getAnchors()) {
				checkOutdatedDeviceStatus(anchorStatus);
			}
			for (DeviceStatus tagStatus : network.getTags()) {
				checkOutdatedDeviceStatus(tagStatus);
			}
		});
	}

	private void checkOutdatedDeviceStatus(DeviceStatus deviceStatus) {
		long now = new Date().getTime();
		if (new Date((now - InfoWebSocket.OUTDATED_DEVICE_STATUS_MILISECONDS)).after(deviceStatus.getLastTimeUpdated())) {
			deviceStatus.setStatus(DeviceStatus.Status.OFFLINE);
		}
	}

	private void prepareUpload(String message) throws IOException {
		UpdateRequest updateRequest = objectMapper.readValue(message, UpdateRequest.class);
		Map<Integer, Set<Integer>> sinkToDevicesMap = askForFileList(updateRequest);

		byte[] bytes = DatatypeConverter.parseBase64Binary(updateRequest.getBase64file().split(",")[1]);
		for (Map.Entry<Integer, Set<Integer>> entry : sinkToDevicesMap.entrySet()) {
			Optional<Network> networkOptional = networkController.getBySinkShortId(entry.getKey());
			networkOptional.ifPresent(network -> {
				entry.getValue().forEach((Integer shortId) -> {
					network.getToUpdateIds().add(shortId);
				});
				applyOnAskListResponse(network, bytes);
			});
		}
	}

	private void applyOnAskListResponse(Network network, byte[] bytes) {
		network.getFileListSummaryFuture().whenComplete(((fileListSummary, fileListException) -> {
			try {
				if (fileListSummary != null) {
					if (fileListSummary.getFreeSpace() >= bytes.length) {
						doUpload(network, fileListSummary, bytes);
					} else {
						network.setFileDeletionStatus(new CompletableFuture<>());
						applyOnDelete(network, bytes);
						removeRedundantFile(network, fileListSummary);
					}
				} else {
					sendErrorCode("IWS_007");
				}
			} catch (IOException e) {
				e.printStackTrace();
				sendErrorCode("IWS_010");
			} finally {
				network.setFileListSummaryFuture(new CompletableFuture<>());
			}
		}));
	}

	private void applyOnAskListResponse(Network network) {
		network.getFileListSummaryFuture().whenComplete(((fileListSummary, fileListException) -> {
			if (fileListSummary != null) {
				Optional<FileListDetails> fileOptional = fileListSummary.getFiles().stream()
					.filter(fileListDetails -> fileListDetails.getPath().equals(network.getFileName()))
					.findFirst();
				if (fileOptional.isPresent()) {
					FileListDetails fileListDetails = fileOptional.get();
					try {
						String md5 = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(network.getFile())).toUpperCase();
						Crc16 crc = new Crc16(0x1DB7);
						if (fileListDetails.getMd5().equals(md5) && fileListDetails.getCrc() == crc.calculate(network.getFile())) {
							sendStartUpgrade(network, fileListDetails.getPath());
						} else {
							sendErrorCode("IWS_006");
						}
					} catch (NoSuchAlgorithmException | IOException e) {
						e.printStackTrace();
						sendErrorCode("IWS_010");
					} finally {
						network.setFileListSummaryFuture(new CompletableFuture<>());
					}
				}
			} else {
				sendErrorCode("IWS_007");
			}
		}));
	}

	private void applyOnDelete(Network network, byte[] bytes) {
		network.getFileDeletionStatus().whenComplete((deleted, deleteException) -> {
			if (deleted != null && deleted.getSuccess()) {
				try {
					network.setFileListSummaryFuture(new CompletableFuture<>());
					applyOnAskListResponse(network, bytes);
					sendAskForFileList(network);
				} catch (IOException e) {
					sendErrorCode("IWS_010");
					e.printStackTrace();
				}
			} else {
				sendErrorCode("IWS_005");
			}
		});
	}

	private void doUpload(Network network, FileListSummary fileListSummary, byte[] bytes) throws IOException {
		List<FileInfo> uploads = prepareToSendFile(fileListSummary, bytes, network);
		network.setFile(bytes);
		sendFilePart(network, uploads, 0);
	}

	private List<FileInfo> prepareToSendFile(FileListSummary fileListSummary, byte[] bytes, Network network) throws IOException {
		int buffSize = fileListSummary.getBuffSize();
		int stepSize = -1, offset = 0;
		List<FileInfo> uploads = new ArrayList<>();
		String fileName = Long.toHexString(Double.doubleToLongBits(Math.random()));
		network.setFileName(fileName);
		for (int i = 0; stepSize != 0; i += stepSize) {
			FileInfo info = new FileInfo();
			Upload upload = new Upload(fileName, bytes.length, offset, 0, "");
			info.setArgs(upload);
			stepSize = buffSize - JsonHelper.calculateJsonLength(objectMapper.writeValueAsString(Collections.singletonList(info)), i);
			stepSize = stepSize * 3 / 4 - 4;
			stepSize -= (int) Math.ceil(Math.log10(stepSize + 1));
			if (offset + stepSize > bytes.length) {
				stepSize = bytes.length - offset;
			}
			if (stepSize > 0) {
				upload.setDataSize(stepSize);
				offset += upload.getDataSize();
				upload.setData(DatatypeConverter.printBase64Binary(Arrays.copyOfRange(bytes, i, i + stepSize)));
				uploads.add(info);
			}
		}
		return uploads;
	}

	private void sendFilePart(Network network, List<FileInfo> uploads, int currentIndex) {
		managedExecutorService.execute(() -> {
			try {
				String dataToSend = objectMapper.writeValueAsString(Collections.singletonList(uploads.get(currentIndex)));
				network.getSession().getBasicRemote().sendText(dataToSend);

				FileAcknowledge acknowledge = network.getFileUploadFuture().get(InfoWebSocket.TIMEOUT_SECONDS_ACK, TimeUnit.SECONDS);
				network.setFileUploadFuture(new CompletableFuture<>());
				if ((acknowledge.getOffset() + acknowledge.getDataSize()) < network.getFile().length) {
					sendFilePart(network, uploads, currentIndex + 1);
				} else {
					network.setFileListSummaryFuture(new CompletableFuture<>());
					applyOnAskListResponse(network);
					sendAskForFileList(network);
				}
			} catch (InterruptedException | ExecutionException | IOException e) {
				sendErrorCode("IWS_010");
				e.printStackTrace();
			} catch (TimeoutException e) {
				sendErrorCode("IWS_004");
			}
		});
	}

	private void handleFileMessage(Session session, Info info) {
		InfoCode infoCode = objectMapper.convertValue(info.getArgs(), InfoCode.class);
		FileInfoType fileInfoType = FileInfoType.from(infoCode.getCode());
		switch (fileInfoType) {
			case INFO:
				break;
			case LIST:
				FileListSummary fileListSummary = objectMapper.convertValue(info.getArgs(), FileListSummary.class);
				networkController.resolve(session, fileListSummary);
				break;
			case DELETED:
				Deleted deleted = objectMapper.convertValue(info.getArgs(), Deleted.class);
				networkController.resolve(session, deleted);
				break;
			case ACK:
				FileAcknowledge acknowledge = objectMapper.convertValue(info.getArgs(), FileAcknowledge.class);
				networkController.resolve(session, acknowledge);
				break;
		}
	}

	private void handleBroadcast(Session session, Info info) {
		DeviceConnected deviceConnected = objectMapper.convertValue(info.getArgs(), DeviceConnected.class);
		Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(deviceConnected.getShortId());
		if (deviceStatusOptional.isPresent() && deviceStatusOptional.get().getStatus() == DeviceStatus.Status.RESTARTING) {
			DeviceStatus deviceStatus = deviceStatusOptional.get();
			deviceStatus.setRestartCount(deviceStatus.getRestartCount() + 1);
			if (isProperFirmwareVersion(deviceConnected) && deviceStatus.getRestartCount() == 2) {
				deviceStatus.setRestartCount(0);
				Optional<? extends Device> optionalByShortId = deviceService.findOptionalByShortId(deviceConnected.getShortId());
				if (optionalByShortId.isPresent()) {
					optionalByShortId.get().setPartition(Device.getPartition(deviceConnected.getFirmwareMinor()));
					deviceRepository.save(optionalByShortId.get());
					deviceStatus.getUpdateFinished().complete(null);
				}
			} else if (deviceStatus.getRestartCount() == 2) {
				deviceStatus.setStatus(DeviceStatus.Status.ONLINE);
				deviceStatus.setRestartCount(0);
				sendErrorCode("IWS_011");
			}
		} else {
			final Optional<DeviceStatus> newDeviceOptional = registerNewDevice(session, deviceConnected);
			getClientSessions().forEach(clientSession -> {
				try {
					if (newDeviceOptional.isPresent()) {
						clientSession.getBasicRemote().sendText(objectMapper.writeValueAsString(
							new InfoWrapper(Collections.singleton(newDeviceOptional.get()))
						));
					}
				} catch (IOException e) {
					sendErrorCode("IWS_010");
					e.printStackTrace();
				}
			});
		}
	}

	private void handleFirmwareUpdate(Session session, Info info) {
		InfoCode infoCode = objectMapper.convertValue(info.getArgs(), InfoCode.class);
		UpdateInfoType updateInfoType = UpdateInfoType.from(infoCode.getCode());
		switch (updateInfoType) {
			case INFO:
				UpdateInfoCode updateInfoCode = objectMapper.convertValue(info.getArgs(), UpdateInfoCode.class);
				UpdateInfoCodeType updateInfoCodeType = UpdateInfoCodeType.from(updateInfoCode.getICode());
				if (updateInfoCodeType != null && updateInfoCodeType.equals(UpdateInfoCodeType.ABORTED)) {
					Integer shortId = updateInfoCode.getShortId();
					Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(shortId);
					if (deviceStatusOptional.isPresent()) {
						DeviceStatus deviceStatus = deviceStatusOptional.get();
						deviceStatus.setStatus(DeviceStatus.Status.ONLINE);
						getClientSessions().forEach(clientSession -> {
							try {
								clientSession.getBasicRemote().sendText(objectMapper.writeValueAsString(
									new InfoWrapper(Collections.singleton(deviceStatus))
								));
								sendErrorCode("IWS_009", deviceStatus);
							} catch (IOException e) {
								sendErrorCode("IWS_010");
								e.printStackTrace();
							}
						});
					}
				}
			break;
			case ACK:
				UpdateAcknowledge updateAcknowledge = objectMapper.convertValue(info.getArgs(), UpdateAcknowledge.class);
				handleFirmwareAck(session, updateAcknowledge);
				break;
		}
	}

	private void handleVersion(Info info) {
		Version version = objectMapper.convertValue(info.getArgs(), Version.class);
		Optional<? extends Device> optionalByShortId = deviceService.findOptionalByShortId(version.getShortId());
		if (optionalByShortId.isPresent()) {
			final Device device = optionalByShortId.get();
			String[] firmwareVersion = version.getFirmwareVersion().split("\\.");
			device.setMajor(Integer.parseInt(firmwareVersion[0]));
			device.setMinor(Integer.parseInt(firmwareVersion[1]));
			device.setCommitHash(firmwareVersion[2]);
			deviceRepository.save(device);
			Optional<DeviceStatus> deviceStatus = networkController.getDeviceStatus(device.getShortId());
			deviceStatus.ifPresent(ds -> ds.setDevice(new DeviceDto(device)));
		}
	}

	private boolean isProperFirmwareVersion(DeviceConnected deviceConnected) {
		Optional<? extends Device> deviceOptional = deviceService.findOptionalByShortId(deviceConnected.getShortId());
		if (deviceOptional.isPresent()) {
			Device device = deviceOptional.get();
			return device.getPartition() != Device.getPartition(deviceConnected.getFirmwareMinor());
		}
		return false;
	}

	private void handleFirmwareAck(Session session, UpdateAcknowledge updateAcknowledge) {
		if (updateAcknowledge.getToward() == 0) {
			Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(updateAcknowledge.getShortId());
			managedExecutorService.execute(() -> {
				if (deviceStatusOptional.isPresent()) {
					DeviceStatus deviceStatus = deviceStatusOptional.get();
					try {
						deviceStatus.setStatus(DeviceStatus.Status.RESTARTING);

						// if after AFTER_UPDATE_WAIT_TIME_SECONDS future is completed then it means update successfuly applied
						deviceStatus.getUpdateFinished().get(AFTER_UPDATE_WAIT_TIME_SECONDS, TimeUnit.SECONDS);

						deviceStatus.setStatus(DeviceStatus.Status.UPDATED);
						for (Session clientSession : getClientSessions()) {
							clientSession.getBasicRemote().sendText(
								objectMapper.writeValueAsString(new InfoWrapper(Collections.singleton(deviceStatus)))
							);
						}
						deviceStatus.setStatus(DeviceStatus.Status.ONLINE);
						deviceStatus.setLastTimeUpdated(new Date());

						Optional<Network> bySession = networkController.getBySession(session);

						if (bySession.isPresent()) {
							Network network = bySession.get();
							if (network.getToUpdateIds().peek() != null) {
								sendStartUpgrade(network, network.getFileName());
							}

							network.checkIfLastThenClear();
						}
					} catch (InterruptedException | ExecutionException | IOException e) {
						sendErrorCode("IWS_010");
						e.printStackTrace();
					} catch (TimeoutException e) {
						sendErrorCode("IWS_004");
					} finally {
						deviceStatus.setUpdateFinished(new CompletableFuture<>());
					}
				}
			});
		}
	}

	private Optional<DeviceStatus> registerNewDevice(Session session, DeviceConnected deviceConnected) {
		Optional<? extends Device> deviceOptional = deviceService.findOptionalByShortId(deviceConnected.getShortId());
		if (deviceOptional.isPresent()) {
			Device device = deviceOptional.get();
			List<Integer> route = deviceConnected.getRoute();
			DeviceStatus deviceStatus = new DeviceStatus(new DeviceDto(device), DeviceStatus.Status.ONLINE);
			if (device instanceof Sink) {
				networkController.registerSink(session, deviceStatus);
			} else if (device instanceof Tag) {
				Optional<Network> networkOptional = networkController.getBySession(session);
				if (networkOptional.isPresent()) {
					networkOptional.get().getTags().add(deviceStatus);
				} else {
					deviceStatus = null;
				}
			} else if (device instanceof Anchor) {
				Optional<Network> networkOptional = networkController.getBySession(session);
				if (networkOptional.isPresent()) {
					networkOptional.get().getAnchors().add(deviceStatus);
				} else {
					deviceStatus = null;
				}
			}
			device.setPartition(Device.getPartition(deviceConnected.getFirmwareMinor()));
			for (int i = 0; i < route.size(); i++) {
				Optional<? extends Device> routeDeviceOptional = deviceService.findOptionalByShortId(route.get(i));
				if (routeDeviceOptional.isPresent()) {
					Device routeDevice = routeDeviceOptional.get();
					device.getRoute().add(new RoutePart(routeDevice.getShortId(), i, device));
				}
			}
			deviceRepository.save(device);
			return Optional.ofNullable(deviceStatus);
		}
		return Optional.empty();
	}

	private void removeRedundantFile(Network network, FileListSummary fileListSummary) throws IOException {
		fileListSummary.getFiles().sort(Comparator.comparing(FileListDetails::getCreatedUTC));
		String path = fileListSummary.getFiles().get(0).getPath();
		Info info = new FileInfo();
		info.setArgs(new Delete(path));
		network.getSession().getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
	}

	private Map<Integer, Set<Integer>> askForFileList(UpdateRequest updateRequest) throws IOException {
		List<Integer> notFoundDevices = new ArrayList<>();
		List<Integer> notConnectedDevices = new ArrayList<>();
		List<Integer> anchorsThatAreNotAssignedToAnySink = new ArrayList<>();
		Map<Integer, Set<Integer>> sinkToDevicesMap = new HashMap<>();
		Info info = new FileInfo();
		info.setArgs(new AskList(""));
		for (Integer shortId : updateRequest.getDevicesShortIds()) {
			Optional<? extends Device> optionalDevice = deviceService.findOptionalByShortId(shortId);
			if (optionalDevice.isPresent()) {
				Device device = optionalDevice.get();
				if (device instanceof Sink) {
					sendAskForFileList(shortId, info, notConnectedDevices);
					updateSinkToDevicesMap(sinkToDevicesMap, shortId, shortId);
				} else if (device instanceof Anchor) {
					Anchor anchor = (Anchor) device;
					if (anchor.getSink() == null) {
						anchorsThatAreNotAssignedToAnySink.add(shortId);
					} else {
						sendAskForFileList(anchor.getSink().getShortId(), info, notConnectedDevices);
						updateSinkToDevicesMap(sinkToDevicesMap, anchor.getSink().getShortId(), anchor.getShortId());
					}
				} else if (device instanceof Tag) {
					Optional<Network> networkByTagOptional = networkController.getByTagShortId(device.getShortId());
					if (networkByTagOptional.isPresent()) {
						sendAskForFileList(networkByTagOptional.get().getSink().getDevice().getShortId(), info, notConnectedDevices);
						updateSinkToDevicesMap(sinkToDevicesMap, networkByTagOptional.get().getSink().getDevice().getShortId(), device.getShortId());
					} else {
						sendErrorCode("IWS_008");
					}
				}
			} else {
				notFoundDevices.add(shortId);
			}
		}

		if (notFoundDevices.size() > 0) {
			sendErrorCode("IWS_001");
		}

		if (notConnectedDevices.size() > 0) {
			sendErrorCode("IWS_002");
		}

		if (anchorsThatAreNotAssignedToAnySink.size() > 0) {
			sendErrorCode("IWS_003");
		}

		return sinkToDevicesMap;
	}

	private void updateSinkToDevicesMap(Map<Integer, Set<Integer>> sinkToDevicesMap, Integer key, Integer value) {
		if (!sinkToDevicesMap.containsKey(key)) {
			sinkToDevicesMap.put(key, new HashSet<>());
		}
		sinkToDevicesMap.get(key).add(value);
	}

	private void sendAskForFileList(Integer shortId, Info info, List<Integer> notConnectedDevicesShortIds) throws IOException {
		Optional<Network> networkOptional = networkController.getBySinkShortId(shortId);
		if (networkOptional.isPresent()) {
			Session session = networkOptional.get().getSession();
			session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
		} else {
			notConnectedDevicesShortIds.add(shortId);
		}
	}

	private void sendAskForFileList(Network network) throws IOException {
		Info info = new FileInfo();
		info.setArgs(new AskList(""));
		Session session = network.getSession();
		session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
	}

	private void sendStartUpgrade(Network network, String path) throws IOException {
		Info info = new UpdateInfo();
		Integer toUpdateId = network.getToUpdateIds().poll();
		Optional<? extends Device> toUpdateOptional = deviceService.findOptionalByShortId(toUpdateId);
		if (toUpdateOptional.isPresent()) {
			Device toUpdate = toUpdateOptional.get();
			List<Integer> route = toUpdate.getRoute().stream().map(RoutePart::getDeviceShortId).collect(Collectors.toList());
			info.setArgs(new Start(toUpdate.getShortId(), route, path, toUpdate.getReversedPartition().getValue()));
			Session session = network.getSession();
			session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
			Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(toUpdate.getShortId());
			deviceStatusOptional.ifPresent((deviceStatus) -> {
					deviceStatus.setStatus(DeviceStatus.Status.UPDATING);
					deviceStatus.setLastTimeUpdated(new Date());
				}
			);
		}
	}

	private void sendErrorCode(String code) {
		sendErrorCode(code, null);
	}

	private void sendErrorCode(String code, DeviceStatus deviceStatus) {
		if (deviceStatus != null) {
			deviceStatus.setStatus(DeviceStatus.Status.ONLINE);
		}
		for (Session clientSession : clientSessions) {
			try {
				InfoErrorWrapper infoErrorWrapper;
				infoErrorWrapper = Optional.ofNullable(deviceStatus).map(ds -> new InfoErrorWrapper(code, ds)).orElseGet(() -> new InfoErrorWrapper(code));
				clientSession.getBasicRemote().sendText(objectMapper.writeValueAsString(infoErrorWrapper));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
