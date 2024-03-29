package co.blastlab.indoornavi.socket.info;

import co.blastlab.indoornavi.dao.repository.DeviceRepository;
import co.blastlab.indoornavi.domain.*;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.dto.uwb.UwbDto;
import co.blastlab.indoornavi.service.UwbService;
import co.blastlab.indoornavi.socket.WebSocket;
import co.blastlab.indoornavi.socket.info.client.CheckBatteryLevel;
import co.blastlab.indoornavi.socket.info.client.ClientRequest;
import co.blastlab.indoornavi.socket.info.client.RawCommand;
import co.blastlab.indoornavi.socket.info.client.UpdateRequest;
import co.blastlab.indoornavi.socket.info.controller.*;
import co.blastlab.indoornavi.socket.info.helper.Crc16;
import co.blastlab.indoornavi.socket.info.helper.JsonHelper;
import co.blastlab.indoornavi.socket.info.server.Info;
import co.blastlab.indoornavi.socket.info.server.Info.InfoType;
import co.blastlab.indoornavi.socket.info.server.InfoCode;
import co.blastlab.indoornavi.socket.info.server.broadcast.DeviceConnected;
import co.blastlab.indoornavi.socket.info.server.command.BatteryLevel;
import co.blastlab.indoornavi.socket.info.server.command.DeviceTurnOn;
import co.blastlab.indoornavi.socket.info.server.command.VersionCommand;
import co.blastlab.indoornavi.socket.info.server.file.FileInfo;
import co.blastlab.indoornavi.socket.info.server.file.FileInfo.FileInfoType;
import co.blastlab.indoornavi.socket.info.server.file.in.Deleted;
import co.blastlab.indoornavi.socket.info.server.file.in.FileAcknowledge;
import co.blastlab.indoornavi.socket.info.server.file.in.FileListDetails;
import co.blastlab.indoornavi.socket.info.server.file.in.FileListSummary;
import co.blastlab.indoornavi.socket.info.server.file.out.AskList;
import co.blastlab.indoornavi.socket.info.server.file.out.Delete;
import co.blastlab.indoornavi.socket.info.server.file.out.Upload;
import co.blastlab.indoornavi.socket.info.server.update.UpdateInfo;
import co.blastlab.indoornavi.socket.info.server.update.UpdateInfo.UpdateInfoType;
import co.blastlab.indoornavi.socket.info.server.update.UpdateInfoCode;
import co.blastlab.indoornavi.socket.info.server.update.UpdateInfoCode.UpdateInfoCodeType;
import co.blastlab.indoornavi.socket.info.server.update.in.UpdateAcknowledge;
import co.blastlab.indoornavi.socket.info.server.update.out.Start;
import co.blastlab.indoornavi.socket.info.server.version.Version;
import co.blastlab.indoornavi.socket.wrappers.BatteryLevelsWrapper;
import co.blastlab.indoornavi.socket.wrappers.InfoErrorWrapper;
import co.blastlab.indoornavi.socket.wrappers.InfoWrapper;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static co.blastlab.indoornavi.socket.info.controller.DeviceStatus.*;
import static co.blastlab.indoornavi.socket.info.controller.DeviceStatus.Status.*;

@ServerEndpoint(value = "/info")
@Singleton
@Startup
public class InfoWebSocket extends WebSocket {

	@Inject
	private Logger logger;

	// this logger is used by callback executed by timer outside the context
	private Logger loggerNoCDI = new Logger();

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
		IWS_012 - The file format is invalid. Only IHS files are allowed.
		IWS_013 - Firmware version or given partition is wrong.
		IWS_014 - Device didn't answer after 2 times asked about version during restart.
	 */

	private final static long TIMEOUT_SECONDS_ACK = 30;
	private final static long AFTER_UPDATE_WAIT_TIME_SECONDS = 60;
	private final static long OUTDATED_DEVICE_STATUS_MILLISECONDS = 30_000;
	private final static long OUTDATED_DEVICE_STATUS_CHECK_MILLISECONDS = 90_000;

	@Inject
	private DeviceRepository deviceRepository;

	@Inject
	private UwbService uwbService;

	@Inject
	private NetworkController networkController;

	@Inject
	private CommandController commandController;

	@Inject
	private BatteryLevelController batteryLevelController;

	@Resource
	private ManagedExecutorService managedExecutorService;

	@Resource
	private TimerService timerService;

	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = new HashMap<>();
	private static Set<Session> frontendSessions = new HashSet<>();

	// key: sink shortId, value: session
	private static Map<Integer, Session> sinkSessions = new HashMap<>();

	private ObjectMapper objectMapper = new ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

	public void assignSinkShortIdToSession(Session session, Integer shortId) {
		sinkSessions.put(shortId, session);
	}

	public Session getSinkSession(Integer sinkShortId) {
		return sinkSessions.get(sinkShortId);
	}

	public Optional<Integer> getSinkShortIdBySession(Session session) {
		Integer[] keys = sinkSessions.entrySet()
			.stream()
			.filter(entry -> Objects.equals(entry.getValue().getId(), session.getId()))
			.map(Map.Entry::getKey).distinct().toArray(Integer[]::new);
		return Optional.ofNullable(keys.length > 0 ? keys[0] : null);
	}

	@Override
	public Set<Session> getFrontendSessions() {
		return frontendSessions;
	}

	@Override
	protected Set<Session> getSinkSessions() {
		return new HashSet<>(sinkSessions.values());
	}

	@Override
	protected Map<Long, String> getThreadToSessionMap() {
		return threadIdToSessionId;
	}

	@PostConstruct
	void init() {
		logger.trace("Creating interval timer");
		timerService.createIntervalTimer(0, OUTDATED_DEVICE_STATUS_CHECK_MILLISECONDS, new TimerConfig(null, false));
	}

	@Timeout
	public void updateStatuses() {
		loggerNoCDI.trace("Updating device statuses");
		networkController.getNetworks().forEach(network -> {
			for (DeviceStatus anchorStatus : network.getAnchors()) {
				checkOutdatedDeviceStatus(anchorStatus, network.getSession());
			}
			for (DeviceStatus tagStatus : network.getTags()) {
				checkOutdatedDeviceStatus(tagStatus, network.getSession());
			}
			checkOutdatedDeviceStatus(network.getSink(), network.getSession());
		});
		getFrontendSessions().forEach(this::sendInfoAboutConnectedDevices);
	}

	@OnOpen
	public void open(Session session) {
		super.open(session, () -> {
			sendInfoAboutConnectedDevices(session);
		}, () -> {
			commandController.sendHandShake(session);
		});
	}

	@OnClose
	public void close(Session session) {
		super.close(session, () -> {}, () -> {
			networkController.unregister(session);
		});
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		setSessionThread(session);
		logger.setId(getSessionId());

		if (isSinkSession(session)) {
			logger.trace("Received message from server {}", message);
			List<Info> infos = objectMapper.readValue(message, new TypeReference<List<Info>>() {});
			for (Info info : infos) {
				if (info.getCode() == null) {
					logger.debug("This message is invalid {}", message);
					continue;
				}
 				Optional.ofNullable(InfoType.from(info.getCode())).ifPresent(infoType -> {
					switch (infoType) {
						case STATION_WAKE_UP:
							break;
						case STATION_SLEEP:
							break;
						case VERSION:
							handleVersion(info);
							break;
						case BROADCAST:
							handleBroadcast(info);
							break;
						case STATUS:
							break;
						case FIRMWARE_UPDATE:
							handleFirmwareUpdate(session, info);
							break;
						case FILE:
							handleFileMessage(session, info);
							break;
						case COMMAND:
							commandController.handleServerCommand(session, info);
							break;
					}
				});
			}
		} else if (isFrontendSession(session)) {
			logger.trace("[{}] Received message from client {}", getSessionId(), message);
			ClientRequest clientRequest = objectMapper.readValue(message, ClientRequest.class);
			switch (clientRequest.getType()) {
				case CHECK_BATTERY_LEVEL:
					List<CheckBatteryLevel> checkBatteryLevel = objectMapper.convertValue(clientRequest.getArgs(), new TypeReference<List<CheckBatteryLevel>>() {});
					List<BatteryLevel> batteryLevels = batteryLevelController.checkBatteryLevels(checkBatteryLevel);
					if (batteryLevels.size() > 0) {
						broadCastMessage(Collections.singleton(session), new BatteryLevelsWrapper(batteryLevels));
					}
					break;
				case RAW_COMMAND:
					RawCommand rawCommand = objectMapper.convertValue(clientRequest.getArgs(), RawCommand.class);
					commandController.handleRawCommand(rawCommand, session);
					break;
				case UPDATE_FIRMWARE:
					prepareUpload(clientRequest);
					break;
			}
		}
	}

	public void onDeviceTurnOn(Session session, DeviceTurnOn deviceTurnOn) {
		Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(deviceTurnOn.getDeviceShortId());
		deviceStatusOptional.ifPresent(deviceStatus -> {
			if (deviceStatus.getStatus() == RESTARTING) {
				logger.trace("Device is restarting after update ({})", deviceStatus.getRestartCount());
				deviceStatus.setRestartCount(deviceStatus.getRestartCount() + 1);
				if (isProperFirmwareVersion(deviceTurnOn) && deviceStatus.getRestartCount() == 2) {
					deviceStatus.setRestartCount(0);
					uwbService.findOptionalByShortId(deviceTurnOn.getDeviceShortId()).ifPresent((device) -> {
						device.setMinor(deviceTurnOn.getFirmwareMinor());
						deviceRepository.save(device);
						deviceStatus.getUpdateFinished().complete(null);
						logger.trace("Device {} has been updated", deviceStatus.getDevice());
					});
				} else if (deviceStatus.getRestartCount() == 2) {
					logger.trace("Device restarted 2 times but has wrong firmware");
					deviceStatus.setStatus(ONLINE);
					deviceStatus.setRestartCount(0);
					sendErrorCode("IWS_011");
				}
			} else if (deviceStatus.getStatus() == OFFLINE) {
				deviceStatus.setStatus(ONLINE);
				deviceStatus.setRestartCount(0);
				deviceStatus.setCheckVersionAfterRestartCount(0);
				uwbService.findOptionalByShortId(deviceTurnOn.getDeviceShortId()).ifPresent((device) -> {
					device.setMinor(deviceTurnOn.getFirmwareMinor());
					deviceRepository.save(device);
				});
				broadCastMessage(getFrontendSessions(), new InfoWrapper(Collections.singleton(deviceStatus)));
			} else if (deviceStatus.getStatus() == ONLINE) {
				uwbService.findOptionalByShortId(deviceTurnOn.getDeviceShortId()).ifPresent((device) -> {
					device.setMinor(deviceTurnOn.getFirmwareMinor());
					deviceRepository.save(device);
				});
			}
		});

		if (!deviceStatusOptional.isPresent()) {
			final Optional<DeviceStatus> newDeviceOptional = registerNewDevice(session, deviceTurnOn);
			newDeviceOptional.ifPresent(
				deviceStatus ->
					broadCastMessage(
						getFrontendSessions(),
						new InfoWrapper(Collections.singleton(deviceStatus)),
						() -> sendErrorCode("IWS_010")
					)
			);
		}
	}

	private void checkOutdatedDeviceStatus(DeviceStatus deviceStatus, Session session) {
		if ((isOutdated(deviceStatus.getLastTimeUpdated()) && ONLINE.equals(deviceStatus.getStatus()))) {
			deviceStatus.setStatus(OFFLINE);
		} else if ((isOutdated(deviceStatus.getRestartingStartedTime()) && RESTARTING.equals(deviceStatus.getStatus()))) {
			if (deviceStatus.getCheckVersionAfterRestartCount() < 2) {
				deviceStatus.setCheckVersionAfterRestartCount(deviceStatus.getCheckVersionAfterRestartCount() + 1);
				commandController.sendHandShake(session, deviceStatus.getDevice().getShortId());
			} else {
				deviceStatus.setCheckVersionAfterRestartCount(0);
				deviceStatus.setStatus(OFFLINE);
				sendErrorCode("IWS_014", deviceStatus);
			}
		}
	}

	private boolean isOutdated(Date dateToCheck) {
		if (dateToCheck == null) {
			return false;
		}
		long now = new Date().getTime();
		return new Date((now - InfoWebSocket.OUTDATED_DEVICE_STATUS_MILLISECONDS)).after(dateToCheck);
	}

	private void sendInfoAboutConnectedDevices(Session session) {
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

		broadCastMessage(ImmutableSet.of(session), new InfoWrapper(devices));
	}

	private void prepareUpload(ClientRequest clientRequest) throws IOException {
		logger.setId(getSessionId());
		logger.trace("Trying to prepare upload");
		UpdateRequest updateRequest = objectMapper.convertValue(clientRequest.getArgs(), UpdateRequest.class);
		logger.trace("Update request is: {}", updateRequest);
		Map<Integer, Set<Integer>> sinkToDevicesMap = askForFileList(updateRequest);

		byte[] bytes = DatatypeConverter.parseBase64Binary(updateRequest.getBase64file().split(",")[1]);
		if (isProperFileExtension(bytes)) {
			for (Map.Entry<Integer, Set<Integer>> entry : sinkToDevicesMap.entrySet()) {
				Optional<Network> networkOptional = networkController.getBySinkShortId(entry.getKey());
				networkOptional.ifPresent(network -> {
					entry.getValue().forEach((Integer shortId) -> {
						network.getToUpdateIds().add(shortId);
					});
					applyOnAskListResponse(network, bytes);
				});
			}
		} else {
			sendErrorCode("IWS_012");
		}
	}

	/**
	 * This method is applied in early stage of update process.
	 * It will check free disk space on the sink and if there is no free space it will recursively remove files to let the update process to continue.
	 */
	private void applyOnAskListResponse(Network network, byte[] bytes) {
		logger.setId(getSessionId()).trace("Waiting for the file list summary to decide if next step is update or free up space on device");
		network.getFileListSummaryFuture().whenComplete(((fileListSummary, fileListException) -> {
			try {
				if (fileListSummary != null) {
					logger.setId(getSessionId()).trace("The file list summary received");
					if (fileListSummary.getFreeSpace() >= bytes.length) {
						logger.trace("Doing upload");
						doUpload(network, fileListSummary, bytes);
					} else {
						logger.trace("Deleting files");
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

	/**
	 * This method is applied in late stage of update process.
	 * It will check md5 and crc16 of the uploaded file to make sure that uploading process has been finishied successfully.
	 * If everything is fine, it will send a command to start an update.
	 */
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
						Crc16 crc = new Crc16(0x1021);
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

	/**
	 * This method split the firmware file into small batches. Batch size is calculated by buffor size of the sink and the json frame size.
	 */
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
				broadCastMessage(ImmutableSet.of(network.getSession()), dataToSend);

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

	/**
	 * case LIST goes here when resolved:
	 *
	 * @see InfoWebSocket#applyOnAskListResponse(Network) or
	 * @see InfoWebSocket#applyOnAskListResponse(Network, byte[]) it depends on which one is currently applied
	 * case DELETED goes here when resolved:
	 * @see InfoWebSocket#applyOnDelete(Network, byte[])
	 * case ACK goes here when resolved:
	 * @see InfoWebSocket#sendFilePart(Network, List, int)
	 */
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

	/**
	 * When update is finished for specific device `getUpdateFinished` promise will be resolved and handled here:
	 *
	 * @see InfoWebSocket#handleFirmwareAck(Session, UpdateAcknowledge)
	 */
	private void handleBroadcast(Info info) {
		DeviceConnected deviceConnected = objectMapper.convertValue(info.getArgs(), DeviceConnected.class);
		logger.setId(getSessionId()).trace("Device connected {}", deviceConnected);
		setDeviceRoute(deviceConnected);
	}

	private void handleFirmwareUpdate(Session session, Info info) {
		InfoCode infoCode = objectMapper.convertValue(info.getArgs(), InfoCode.class);
		UpdateInfoType updateInfoType = UpdateInfoType.from(infoCode.getCode());
		switch (updateInfoType) {
			case INFO:
				UpdateInfoCode updateInfoCode = objectMapper.convertValue(info.getArgs(), UpdateInfoCode.class);
				Optional.ofNullable(UpdateInfoCodeType.from(updateInfoCode.getICode())).ifPresent(updateInfoCodeType -> {
					if (updateInfoCodeType.equals(UpdateInfoCodeType.ABORTED) || updateInfoCodeType.equals(UpdateInfoCodeType.BAD_FIRMWARE_VERSION)) {
						Integer shortId = updateInfoCode.getShortId();
						Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(shortId);
						if (deviceStatusOptional.isPresent()) {
							DeviceStatus deviceStatus = deviceStatusOptional.get();
							deviceStatus.setStatus(ONLINE);
							broadCastMessage(getFrontendSessions(), new InfoWrapper(Collections.singleton(deviceStatus)));
							sendErrorCode(updateInfoCodeType.equals(UpdateInfoCodeType.ABORTED) ? "IWS_009" : "IWS_013", deviceStatus);
						}
					}
				});
				break;
			case ACK:
				UpdateAcknowledge updateAcknowledge = objectMapper.convertValue(info.getArgs(), UpdateAcknowledge.class);
				handleFirmwareAck(session, updateAcknowledge);
				break;
		}
	}

	private void handleVersion(Info info) {
		Version version = objectMapper.convertValue(info.getArgs(), Version.class);
		logger.setId(getSessionId()).trace("Received message about device version {}", version);
		Optional<? extends Uwb> optionalByShortId = uwbService.findOptionalByShortId(version.getShortId());
		if (optionalByShortId.isPresent()) {
			final Uwb device = optionalByShortId.get();
			String[] firmwareVersion = version.getFirmwareVersion().split("\\.");
			device.setMajor(Integer.parseInt(firmwareVersion[0]));
			device.setMinor(Integer.parseInt(firmwareVersion[1]));
			device.setCommitHash(firmwareVersion[2]);
			deviceRepository.save(device);
			Optional<DeviceStatus> deviceStatus = networkController.getDeviceStatus(device.getShortId());
			deviceStatus.ifPresent(ds -> ds.setDevice(new AnchorDto((Anchor) device)));
		}
	}

	private boolean isProperFirmwareVersion(DeviceTurnOn deviceTurnOn) {
		Optional<? extends Uwb> deviceOptional = uwbService.findOptionalByShortId(deviceTurnOn.getDeviceShortId());
		if (deviceOptional.isPresent()) {
			Uwb uwb = deviceOptional.get();
			return Uwb.getPartition(uwb.getMinor()) != Uwb.getPartition(deviceTurnOn.getFirmwareMinor());
		}
		return false;
	}

	private void handleFirmwareAck(final Session session, final UpdateAcknowledge updateAcknowledge) {
		logger.setId(session.getId()).trace("Received ACK: {}", updateAcknowledge);
		if (updateAcknowledge.getToward() == 0) {
			managedExecutorService.submit(() -> {
				Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(updateAcknowledge.getShortId());
				if (deviceStatusOptional.isPresent()) {
					DeviceStatus deviceStatus = deviceStatusOptional.get();
					deviceStatus.setStatus(RESTARTING);
					deviceStatus.setRestartingStartedTime(new Date());
					try {
						// if after AFTER_UPDATE_WAIT_TIME_SECONDS future is completed then it means update successfuly applied
						deviceStatus.getUpdateFinished().get(AFTER_UPDATE_WAIT_TIME_SECONDS, TimeUnit.SECONDS);

						deviceStatus.setStatus(Status.UPDATED);
						broadCastMessage(getFrontendSessions(), new InfoWrapper(Collections.singleton(deviceStatus)));
						deviceStatus.setStatus(ONLINE);
						commandController.sendHandShake(session, deviceStatus.getDevice().getShortId());
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
						deviceStatus.setRestartCount(0);
						deviceStatus.setUpdateFinished(new CompletableFuture<>());
					}
				}
			});
		}
	}

	private Optional<DeviceStatus> registerNewDevice(Session session, DeviceTurnOn deviceTurnOn) {
		logger.setId(getSessionId());
		Optional<? extends Uwb> uwbOptional = uwbService.findOptionalByShortId(deviceTurnOn.getDeviceShortId());
		if (uwbOptional.isPresent()) {
			Uwb uwb = uwbOptional.get();

			DeviceStatus deviceStatus = new DeviceStatus(new UwbDto(uwb), ONLINE);
			if (uwb instanceof Sink) {
				logger.trace("Registering sink {}", uwb);
				networkController.registerSink(session, deviceStatus);
			} else if (uwb instanceof Tag) {
				Optional<Network> networkOptional = networkController.getBySession(session);
				if (networkOptional.isPresent()) {
					logger.trace("Registering tag {}", uwb);
					Network network = networkOptional.get();
					network.getTags().removeIf(ds -> OFFLINE.equals(ds.getStatus()));
					network.getTags().add(deviceStatus);
				} else {
					deviceStatus = null;
				}

				deviceRepository.save(uwb);
				commandController.sendHandShake(session, deviceTurnOn.getDeviceShortId());
			} else if (uwb instanceof Anchor) {
				Optional<Network> networkOptional = networkController.getBySession(session);
				if (networkOptional.isPresent()) {
					logger.trace("Registering anchor {}", uwb);
					Network network = networkOptional.get();
					network.getAnchors().removeIf(ds -> OFFLINE.equals(ds.getStatus()));
					network.getAnchors().add(deviceStatus);
				} else {
					deviceStatus = null;
				}

				deviceRepository.save(uwb);
				commandController.sendHandShake(session, deviceTurnOn.getDeviceShortId());
			}
			return Optional.ofNullable(deviceStatus);
		}
		logger.trace("Device not found in database");
		return Optional.empty();
	}

	private void setDeviceRoute(DeviceConnected deviceConnected) {
		uwbService.findOptionalByShortId(deviceConnected.getShortId()).ifPresent(uwb -> {
			List<Integer> route = deviceConnected.getRoute();
			logger.trace("Setting route");
			for (int i = 0; i < route.size(); i++) {
				Optional<? extends Uwb> routeDeviceOptional = uwbService.findOptionalByShortId(route.get(i));
				if (routeDeviceOptional.isPresent()) {
					Uwb routeDevice = routeDeviceOptional.get();
					uwb.getRoute().add(new RoutePart(routeDevice.getShortId(), i, uwb));
				}
			}
			deviceRepository.save(uwb);
		});
	}

	private void removeRedundantFile(Network network, FileListSummary fileListSummary) throws IOException {
		fileListSummary.getFiles().sort(Comparator.comparing(FileListDetails::getCreatedUTC));
		String path = fileListSummary.getFiles().get(0).getPath();
		Info info = new FileInfo();
		info.setArgs(new Delete(path));
		broadCastMessage(ImmutableSet.of(network.getSession()), objectMapper.writeValueAsString(Collections.singletonList(info)));
	}

	private Map<Integer, Set<Integer>> askForFileList(UpdateRequest updateRequest) throws IOException {
		List<Integer> notFoundDevices = new ArrayList<>();
		List<Integer> notConnectedDevices = new ArrayList<>();
		List<Integer> anchorsThatAreNotAssignedToAnySink = new ArrayList<>();
		Map<Integer, Set<Integer>> sinkToDevicesMap = new HashMap<>();

		Info info = new FileInfo();
		info.setArgs(new AskList(""));

		for (Integer shortId : updateRequest.getDevicesShortIds()) {
			Optional<? extends Uwb> optionalDevice = uwbService.findOptionalByShortId(shortId);
			if (optionalDevice.isPresent()) {
				Uwb device = optionalDevice.get();
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
		logger.setId(getSessionId()).trace("Sending ask for file list");
		Optional<Network> networkOptional = networkController.getBySinkShortId(shortId);
		if (networkOptional.isPresent()) {
			Session session = networkOptional.get().getSession();
			broadCastMessage(ImmutableSet.of(session), objectMapper.writeValueAsString(Collections.singletonList(info)));
		} else {
			notConnectedDevicesShortIds.add(shortId);
		}
	}

	private void sendAskForFileList(Network network) throws IOException {
		Info info = new FileInfo();
		info.setArgs(new AskList(""));
		Session session = network.getSession();
		broadCastMessage(ImmutableSet.of(session), objectMapper.writeValueAsString(Collections.singletonList(info)));
	}

	void sendStartUpgrade(Network network, String path) throws IOException {
		Session session = network.getSession();
		logger.setId(session.getId()).trace("Trying to send start upgrade command");
		Info info = new UpdateInfo();
		Integer toUpdateId = network.getToUpdateIds().poll();
		Optional<? extends Uwb> toUpdateOptional = uwbService.findOptionalByShortId(toUpdateId);
		if (toUpdateOptional.isPresent()) {
			Uwb toUpdate = toUpdateOptional.get();
			List<Integer> route = toUpdate.getRoute().stream().map(RoutePart::getDeviceShortId).collect(Collectors.toList());
			info.setArgs(new Start(toUpdate.getShortId(), route, path, toUpdate.getReversedPartition().getValue()));
			broadCastMessage(ImmutableSet.of(session), objectMapper.writeValueAsString(Collections.singletonList(info)));
			Optional<DeviceStatus> deviceStatusOptional = networkController.getDeviceStatus(toUpdate.getShortId());
			deviceStatusOptional.ifPresent((deviceStatus) -> {
					deviceStatus.setStatus(UPDATING);
					deviceStatus.setLastTimeUpdated(new Date());
				}
			);
			logger.trace("Start upgrade command sent");
		}
	}

	private boolean isProperFileExtension(byte[] data) {
		byte[] filePrefix = Arrays.copyOfRange(data, 2, 6);
		int current = ByteBuffer.wrap(filePrefix).getInt();
		int expected = Integer.reverseBytes(0x852A456F);
		return current == expected;
	}

	void sendErrorCode(String code) {
		sendErrorCode(code, null);
	}

	private void sendErrorCode(String code, DeviceStatus deviceStatus) {
		logger.setId(getSessionId()).warn("Sending error code: {}", code);
		InfoErrorWrapper infoErrorWrapper;
		infoErrorWrapper = Optional.ofNullable(deviceStatus).map(ds -> new InfoErrorWrapper(code, ds)).orElseGet(() -> new InfoErrorWrapper(code));
		broadCastMessage(getFrontendSessions(), infoErrorWrapper);
	}

	public void updateVersion(VersionCommand versionCommand) {
		networkController.getDeviceStatus(versionCommand.getShortId()).ifPresent(deviceStatus -> {
			if (deviceStatus.getStatus() == ONLINE || deviceStatus.getStatus() == RESTARTING) {
				Optional<? extends Uwb> optionalByShortId = uwbService.findOptionalByShortId(versionCommand.getShortId());
				if (optionalByShortId.isPresent()) {
					final Uwb device = optionalByShortId.get();
					device.setMajor(versionCommand.getMajor());
					device.setMinor(versionCommand.getMinor());
					device.setCommitHash(versionCommand.getCommitHash());
					deviceRepository.save(device);

					deviceStatus.setDevice(new UwbDto(device));
					deviceStatus.setCheckVersionAfterRestartCount(0);
					deviceStatus.setRestartCount(0);
					broadCastMessage(getFrontendSessions(), new InfoWrapper(Collections.singleton(deviceStatus)));
				}
			}
		});
	}
}
