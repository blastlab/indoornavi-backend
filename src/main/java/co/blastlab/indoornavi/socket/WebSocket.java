package co.blastlab.indoornavi.socket;

import co.blastlab.indoornavi.utils.Logger;

import javax.websocket.Session;
import java.util.Map;
import java.util.Set;

public abstract class WebSocket extends WebSocketCommunication {

	private final Logger logger = new Logger();

	private final static String FRONTEND = "frontend";
	private final static String SINK = "sink";

	protected boolean isFrontendSession(Session session) {
		return session.getRequestParameterMap().containsKey(FRONTEND);
	}

	protected boolean isSinkSession(Session session) {
		return session.getRequestParameterMap().containsKey(SINK);
	}

	protected void open(Session session) {
		logger.setId(getSessionId()).trace("Session opened {}, query params = {}", session.getId(), session.getRequestParameterMap());
		if (session.getRequestParameterMap().containsKey(FRONTEND)) {
			getFrontendSessions().add(session);
		} else if (session.getRequestParameterMap().containsKey(SINK)) {
			getSinkSessions().add(session);
		}
	}

	protected void open(Session session, Runnable doForFrontend, Runnable doForSinks) {
		logger.setId(getSessionId()).trace("Session opened {}, query params = {}", session.getId(), session.getRequestParameterMap());
		if (session.getRequestParameterMap().containsKey(FRONTEND)) {
			getFrontendSessions().add(session);
			doForFrontend.run();
		} else if (session.getRequestParameterMap().containsKey(SINK)) {
			getSinkSessions().add(session);
			doForSinks.run();
		}
	}

	protected void close(Session session) {
		close(session, () -> {}, () -> {});
	}

	protected void close(Session session, Runnable doForClients, Runnable doForServers) {
		logger.setId(getSessionId()).trace("Session closed id = {}, query params = {}", session.getId(), session.getRequestParameterMap());
		if (session.getRequestParameterMap().containsKey(FRONTEND)) {
			getFrontendSessions().remove(session);
			doForClients.run();
		} else if (session.getRequestParameterMap().containsKey(SINK)) {
			getSinkSessions().remove(session);
			doForServers.run();
		}
	}

	protected void setSessionThread(Session session) {
		getThreadToSessionMap().put(Thread.currentThread().getId(), session.getId());
	}

	protected String getSessionId() {
		return getThreadToSessionMap().get(Thread.currentThread().getId());
	}

	protected abstract Set<Session> getFrontendSessions();
	protected abstract Set<Session> getSinkSessions();
	protected abstract Map<Long, String> getThreadToSessionMap();
}
