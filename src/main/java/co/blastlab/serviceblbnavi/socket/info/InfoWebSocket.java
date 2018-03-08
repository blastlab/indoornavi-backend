package co.blastlab.serviceblbnavi.socket.info;

import co.blastlab.serviceblbnavi.dao.repository.DeviceRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.info.client.UpdateRequest;
import co.blastlab.serviceblbnavi.socket.info.future.FutureController;
import co.blastlab.serviceblbnavi.socket.info.future.FutureWrapper;
import co.blastlab.serviceblbnavi.socket.info.server.Info;
import co.blastlab.serviceblbnavi.socket.info.server.Info.InfoType;
import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo;
import co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo.FileInfoType;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.Deleted;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListDetails;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListSummary;
import co.blastlab.serviceblbnavi.socket.info.server.file.out.AskList;
import co.blastlab.serviceblbnavi.socket.info.server.file.out.Delete;
import co.blastlab.serviceblbnavi.socket.info.server.version.Version;
import co.blastlab.serviceblbnavi.socket.wrappers.InfoWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;

@ServerEndpoint("/info")
@Singleton
public class InfoWebSocket extends WebSocket {

	@Inject
	private DeviceRepository deviceRepository;

	@Inject
	private FutureController futureController;

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

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
				// TODO: we need to recognize if the user is on sink, anchor or tag page and then send proper info
				session.getBasicRemote().sendText(objectMapper.writeValueAsString(new InfoWrapper(futureController.getAllSinksIds())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, () -> {});
	}

	@OnClose
	public void close(Session session) {
		super.close(session, () -> {}, () -> {
			futureController.unregister(session);
		});
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (isServerSession(session)) {
			System.out.println(message);
			List<Info> infos = objectMapper.readValue(message, new TypeReference<List<Info>>() {});
			for (Info info : infos) {
				InfoType infoType = InfoType.from(info.getCode());
				switch (infoType) {
					case STATION_WAKE_UP:
						break;
					case STATION_SLEEP:
						break;
					case VERSION:
						handleVersionMessage(session, info);
						break;
					case NEW_DEVICE:
						break;
					case STATUS:
						break;
					case FIRMWARE_UPGRADE:
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

	private void prepareUpload(String message) throws IOException {
		UpdateRequest updateRequest = objectMapper.readValue(message, UpdateRequest.class);
		Set<Integer> sinksShortIds = askForFileList(updateRequest);

		byte[] bytes = DatatypeConverter.parseBase64Binary(updateRequest.getBase64file().split(",")[1]);
		for (Integer sinkShortId : sinksShortIds) {
			Optional<FutureWrapper> futureWrapperOptional = futureController.getBySinkShortId(sinkShortId);
			futureWrapperOptional.ifPresent(futureWrapper -> {
				doUpload(futureWrapper, bytes);
			});
		}
	}

	private void doUpload(FutureWrapper futureWrapper, byte[] bytes) {
		futureWrapper.getFileListSummaryFuture().whenComplete(((fileListSummary, fileListException) -> {
			try {
				if (fileListSummary.getFreeSpace() >= bytes.length) {
					futureWrapper.getSession().getBasicRemote().sendText("");
				} else {
					futureWrapper.getFileDeletionStatus().cancel(false);
					removeRedundantFile(futureWrapper.getSession(), fileListSummary);
					futureWrapper.getFileDeletionStatus().whenComplete((deleted, deleteException) -> {
						if (deleted.getSuccess()) {
							try {
								sendAskForFileList(futureWrapper);
							} catch (IOException e) {
								e.printStackTrace();
							}
							futureWrapper.getFileListSummaryFuture().cancel(false);
							doUpload(futureWrapper, bytes);
						} else {
							// TODO inform user that deletion has been failed
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
	}

	private void handleVersionMessage(Session session, Info info) {
		Version version = objectMapper.convertValue(info.getArgs(), Version.class);
		futureController.register(session, version.getShortId());
		getClientSessions().forEach(clientSession -> {
			try {
				// TODO: send to clients short id of new connected sink as predefined model
				clientSession.getBasicRemote().sendText(String.valueOf(version.getShortId()));
			} catch (IOException e) {
				e.printStackTrace();
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
				futureController.resolve(session, fileListSummary);
				break;
			case DELETED:
				Deleted deleted = objectMapper.convertValue(info.getArgs(), Deleted.class);
				futureController.resolve(session, deleted);
				break;
		}
	}

	private void removeRedundantFile(Session session, FileListSummary fileListSummary) throws IOException {
		fileListSummary.getFiles().sort(Comparator.comparing(FileListDetails::getCreatedUTC).reversed());
		String path = fileListSummary.getFiles().get(0).getPath();
		Info info = new FileInfo();
		info.setArgs(new Delete(path));
		session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
	}

	private Set<Integer> askForFileList(UpdateRequest updateRequest) throws IOException {
		List<Integer> notFoundDevices = new ArrayList<>();
		List<Integer> notConnectedDevices = new ArrayList<>();
		List<Integer> anchorsThatAreNotAssignedToAnySink = new ArrayList<>();
		Set<Integer> sinksShortIds = new HashSet<>();
		Info info = new FileInfo();
		info.setArgs(new AskList(""));
		for (Integer shortId : updateRequest.getDevicesShortIds()) {
			Optional<Device> optionalDevice = deviceRepository.findOptionalByShortId(shortId);
			if (optionalDevice.isPresent()) {
				Device device = optionalDevice.get();
				if (device instanceof Sink) {
					sendAskForFileList(shortId, info, notConnectedDevices, sinksShortIds);
				} else if (device instanceof Anchor) {
					Anchor anchor = (Anchor) device;
					if (anchor.getSink() == null) {
						anchorsThatAreNotAssignedToAnySink.add(shortId);
					} else {
						sendAskForFileList(anchor.getSink().getShortId(), info, notConnectedDevices, sinksShortIds);
					}
				} else if (device instanceof Tag) {
					// TODO: it's too complex for now, we will get back to it later
				}
			} else {
				notFoundDevices.add(shortId);
			}
		}

		if (notFoundDevices.size() > 0) {
			// TODO: send devices that will not be upgraded due to wrong ids
		}

		if (notConnectedDevices.size() > 0) {
			// TODO: send devices that will not be upgraded due to it's state
		}

		if (anchorsThatAreNotAssignedToAnySink.size() > 0) {
			// TODO: send devices that will not be upgraded due to it's state
		}

		return sinksShortIds;
	}

	private void sendAskForFileList(Integer shortId, Info info, List<Integer> notConnectedDevicesShortIds, Set<Integer> sinksShortIds) throws IOException {
		Optional<FutureWrapper> futureWrapperOptional = futureController.getBySinkShortId(shortId);
		if (futureWrapperOptional.isPresent()) {
			Session session = futureWrapperOptional.get().getSession();
			session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
			sinksShortIds.add(shortId);
		} else {
			notConnectedDevicesShortIds.add(shortId);
		}
	}

	private void sendAskForFileList(FutureWrapper futureWrapper) throws IOException {
		Info info = new FileInfo();
		info.setArgs(new AskList(""));
		Session session = futureWrapper.getSession();
		session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
	}
}
