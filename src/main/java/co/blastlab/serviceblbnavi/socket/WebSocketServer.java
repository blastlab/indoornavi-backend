package co.blastlab.serviceblbnavi.socket;

import javax.ejb.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Singleton
@ServerEndpoint("/coordinates")
public class WebSocketServer {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

//	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//	public void getConnectedClients() {
//		System.out.println("-----------------------------------------");
//		System.out.println(clientSessions.size());
//		System.out.println("-----------------------------------------");
//	}

	@OnOpen
	public void open(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			clientSessions.add(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			serverSessions.add(session);
		}
	}

	@OnClose
	public void close(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			clientSessions.remove(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			serverSessions.remove(session);
		}
	}

	@OnError
	public void onError(Throwable error) {
		System.out.print("ERROR");
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			broadCastMessage(serverSessions, message);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			broadCastMessage(clientSessions, message);
		}
	}

	private void broadCastMessage(final Set<Session> sessions, final String message) {
		sessions.forEach(session -> {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
