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

	protected void open(Session session, Runnable doForClients, Runnable doForServers) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().add(session);
			doForClients.run();
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().add(session);
			doForServers.run();
		}
	}

	protected void close(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().remove(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().remove(session);
		}
	}

	protected void close(Session session, Runnable doForClients, Runnable doForServers) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			getClientSessions().remove(session);
			doForClients.run();
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			getServerSessions().remove(session);
			doForServers.run();
		}
	}

	protected abstract Set<Session> getClientSessions();
	protected abstract Set<Session> getServerSessions();
}
