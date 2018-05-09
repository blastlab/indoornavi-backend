package co.blastlab.serviceblbnavi.socket;

import javax.websocket.Session;
import java.util.Set;

public abstract class WebSocket extends WebSocketCommunication {

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	protected boolean isClientSession(Session session) {
		return session.getRequestParameterMap().containsKey(CLIENT);
	}

	protected boolean isServerSession(Session session) {
		return session.getRequestParameterMap().containsKey(SERVER);
	}

	protected void open(Session session, Runnable doForClients, Runnable doForServers) {
		if (session.getRequestParameterMap().containsKey(CLIENT)) {
			getClientSessions().add(session);
			doForClients.run();
		} else if (session.getRequestParameterMap().containsKey(SERVER)) {
			getServerSessions().add(session);
			doForServers.run();
		}
	}

	protected void close(Session session) {
		close(session, () -> {}, () -> {});
	}

	protected void close(Session session, Runnable doForClients, Runnable doForServers) {
		if (session.getRequestParameterMap().containsKey(CLIENT)) {
			getClientSessions().remove(session);
			doForClients.run();
		} else if (session.getRequestParameterMap().containsKey(SERVER)) {
			getServerSessions().remove(session);
			doForServers.run();
		}
	}

	protected abstract Set<Session> getClientSessions();
	protected abstract Set<Session> getServerSessions();
}
