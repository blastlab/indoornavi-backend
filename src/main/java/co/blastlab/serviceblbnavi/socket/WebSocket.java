package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.utils.Logger;

import javax.websocket.Session;
import java.util.Map;
import java.util.Set;

public abstract class WebSocket extends WebSocketCommunication {

	private final Logger logger = new Logger();

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	protected boolean isClientSession(Session session) {
		return session.getRequestParameterMap().containsKey(CLIENT);
	}

	protected boolean isServerSession(Session session) {
		return session.getRequestParameterMap().containsKey(SERVER);
	}

	protected void open(Session session) {
		logger.setId(getSessionId()).trace("Session opened {}, query params = {}", session.getId(), session.getRequestParameterMap());
		if (session.getRequestParameterMap().containsKey(CLIENT)) {
			getClientSessions().add(session);
		} else if (session.getRequestParameterMap().containsKey(SERVER)) {
			getServerSessions().add(session);
		}
	}

	protected void open(Session session, Runnable doForClients, Runnable doForServers) {
		logger.setId(getSessionId()).trace("Session opened {}, query params = {}", session.getId(), session.getRequestParameterMap());
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
		logger.setId(getSessionId()).trace("Session closed id = {}, query params = {}", session.getId(), session.getRequestParameterMap());
		if (session.getRequestParameterMap().containsKey(CLIENT)) {
			getClientSessions().remove(session);
			doForClients.run();
		} else if (session.getRequestParameterMap().containsKey(SERVER)) {
			getServerSessions().remove(session);
			doForServers.run();
		}
	}

	protected void setSessionThread(Session session) {
		getThreadToSessionMap().put(Thread.currentThread().getId(), session.getId());
	}

	protected String getSessionId() {
		return getThreadToSessionMap().get(Thread.currentThread().getId());
	}

	protected abstract Set<Session> getClientSessions();
	protected abstract Set<Session> getServerSessions();
	protected abstract Map<Long, String> getThreadToSessionMap();
}
