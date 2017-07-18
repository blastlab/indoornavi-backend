package co.blastlab.serviceblbnavi.socket;

import javax.websocket.Session;
import java.util.Objects;
import java.util.Set;

public abstract class WebSocket extends WebSocketCommunication {

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	protected boolean isClientSession(Session session) {
		return Objects.equals(session.getQueryString(), CLIENT);
	}

	protected boolean isServerSession(Session session) {
		return Objects.equals(session.getQueryString(), SERVER);
	}

	protected void open(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().add(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().add(session);
		}
	}

	protected void close(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().remove(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().remove(session);
		}
	}

	protected abstract Set<Session> getClientSessions();
	protected abstract Set<Session> getServerSessions();
}
