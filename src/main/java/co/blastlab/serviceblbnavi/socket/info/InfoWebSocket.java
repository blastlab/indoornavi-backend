package co.blastlab.serviceblbnavi.socket.info;

import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.info.Info.InfoType;
import co.blastlab.serviceblbnavi.socket.info.in.FileListSummary;
import co.blastlab.serviceblbnavi.socket.info.out.file.AskList;
import co.blastlab.serviceblbnavi.socket.info.out.file.Delete;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
			Info info = objectMapper.readValue(message, Info.class);
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
					FileListSummary fileListSummary = objectMapper.readValue(message, FileListSummary.class);
					fileListSummaryCompletableFuture.complete(fileListSummary);
					break;
			}
		} else if (isClientSession(session)) {
			doUpload(fileListSummaryCompletableFuture, message);
		}
	}

	private void doUpload(CompletableFuture<FileListSummary> fileListSummaryCompletableFuture, String message) {
		askForFileList(objectMapper);
		fileListSummaryCompletableFuture.thenAccept(fileListSummary -> {
			byte[] bytes = DatatypeConverter.parseBase64Binary(message.split(",")[1]);
			if (fileListSummary.freeSpace >= bytes.length) {
				getServerSessions().forEach(serverSession -> {
//						serverSession.getBasicRemote().sendObject();
				});
			} else {
				removeRedundantFile();
				doUpload(fileListSummaryCompletableFuture, message);
			}
		});
	}

	private void removeRedundantFile() {
		getServerSessions().forEach(session -> {
			try {
				Info info = new Delete("");
				info.setCode(InfoType.FILE.getValue());
				session.getBasicRemote().sendObject(info);
			} catch (IOException | EncodeException e) {
				// TODO: send info about an error
				e.printStackTrace();
			}
		});
	}

	private void askForFileList(ObjectMapper objectMapper) {
		getServerSessions().forEach(session -> {
			try {
				Info info = new AskList("");
				info.setCode(InfoType.FILE.getValue());
				session.getBasicRemote().sendObject(info);
			} catch (IOException | EncodeException e) {
				// TODO: send info about an error
				e.printStackTrace();
			}
		});
	}
}
