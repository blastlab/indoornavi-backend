package co.blastlab.serviceblbnavi.socket;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

// change name after merging issue-97 to master
abstract class WebSocketServerAbstract {
	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	abstract Set<Session> getClientSessions();
	abstract Set<Session> getServerSessions();

	void registerSession(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().add(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().add(session);
		}
	}

	void unregisterSession(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().remove(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().remove(session);
		}
	}

	void broadCastMessage(final Set<Session> sessions, final String message) {
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	boolean isServerSession(Session session) {
		return Objects.equals(session.getQueryString(), SERVER);
	}

	boolean isClientSession(Session session) {
		return Objects.equals(session.getQueryString(), CLIENT);
	}

}
