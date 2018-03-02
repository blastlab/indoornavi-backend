package co.blastlab.serviceblbnavi.socket.info;

import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.info.Info.InfoType;
import co.blastlab.serviceblbnavi.socket.info.in.FileListDetails;
import co.blastlab.serviceblbnavi.socket.info.in.FileListSummary;
import co.blastlab.serviceblbnavi.socket.info.out.file.AskList;
import co.blastlab.serviceblbnavi.socket.info.out.file.Delete;
import co.blastlab.serviceblbnavi.socket.info.out.file.FileInfo;
import co.blastlab.serviceblbnavi.socket.info.out.file.FileInfoType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ServerEndpoint("/info")
@Singleton
public class InfoWebSocket extends WebSocket {

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
		super.open(session, () -> {}, () -> {});
	}

	@OnClose
	public void close(Session session) {
		super.close(session);
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		CompletableFuture<FileListSummary> fileListSummaryCompletableFuture = new CompletableFuture<>();
		if (isServerSession(session)) {
			System.out.println(message);
			List<Info> infos = objectMapper.readValue(message, new TypeReference<List<Info>>(){});
			for (Info info : infos) {
				InfoType infoType = InfoType.from(info.getCode());
				switch (infoType) {
					case STATION_WAKE_UP:
						break;
					case STATION_SLEEP:
						break;
					case SINK_DID:
						break;
					case VERSION:
						break;
					case NEW_DEVICE:
						break;
					case STATUS:
						break;
					case FIRMWARE_UPGRADE:
						break;
					case FILE:
						handleFileMessage(fileListSummaryCompletableFuture, info);
						break;
				}
			}
		} else if (isClientSession(session)) {
			doUpload(fileListSummaryCompletableFuture, message);
		}
	}

	private void doUpload(CompletableFuture<FileListSummary> fileListSummaryCompletableFuture, String message) {
		askForFileList();
		fileListSummaryCompletableFuture.thenAccept(fileListSummary -> {
			byte[] bytes = DatatypeConverter.parseBase64Binary(message.split(",")[1]);
			System.out.println("----------------------------------------------");
			System.out.println(bytes.length);
			System.out.println("----------------------------------------------");
			if (fileListSummary.freeSpace >= bytes.length) {
				for (Session session : getServerSessions()) {
					try {
						session.getBasicRemote().sendText("");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				removeRedundantFile(fileListSummary);
				doUpload(fileListSummaryCompletableFuture, message);
			}
		});
	}

	private void handleFileMessage(CompletableFuture<FileListSummary> fileListSummaryCompletableFuture, Info info) {
		InfoCode infoCode = objectMapper.convertValue(info.getArgs(), InfoCode.class);
		FileInfoType fileInfoType = FileInfoType.from(infoCode.getCode());
		switch (fileInfoType) {
			case INFO:
				break;
			case DOWNLOAD:
				break;
			case ASK_LIST:
				break;
			case LIST:
				FileListSummary fileListSummary = objectMapper.convertValue(info.getArgs(), FileListSummary.class);
				fileListSummaryCompletableFuture.complete(fileListSummary);
				break;
			case DELETE:
				break;
		}
	}

	private void removeRedundantFile(FileListSummary fileListSummary) {
		fileListSummary.getFiles().sort(Comparator.comparing(FileListDetails::getCreatedUTC).reversed());
		String path = fileListSummary.getFiles().get(0).getPath();
		getServerSessions().forEach(session -> {
			try {
				Info info = new FileInfo();
				info.setArgs(new Delete(path));
				session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
			} catch (IOException e) {
				// TODO: send info about an error
				e.printStackTrace();
			}
		});
	}

	private void askForFileList() {
		getServerSessions().forEach(session -> {
			try {
				Info info = new FileInfo();
				info.setArgs(new AskList(""));
				session.getBasicRemote().sendText(objectMapper.writeValueAsString(Collections.singletonList(info)));
			} catch (IOException e) {
				// TODO: send info about an error
				e.printStackTrace();
			}
		});
	}
}
