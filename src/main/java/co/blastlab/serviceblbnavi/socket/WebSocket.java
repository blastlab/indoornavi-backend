package co.blastlab.serviceblbnavi.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.Set;

public abstract class WebSocket extends WebSocketCommunication {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebSocket.class);

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	protected boolean isClientSession(Session session) {
		return session.getRequestParameterMap().containsKey(CLIENT);
	}

	protected boolean isServerSession(Session session) {
		return session.getRequestParameterMap().containsKey(SERVER);
	}

	protected void open(Session session, Runnable doForClients, Runnable doForServers) {
		LOGGER.debug("Session opened {}, query params = {}", session.getId(), session.getRequestParameterMap());
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
		LOGGER.debug("Session closed id = {}, query params = {}", session.getId(), session.getRequestParameterMap());
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
