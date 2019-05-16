package co.blastlab.indoornavi.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class LoggerController {

	private Map<String, Logger> loggers = new HashMap<>();

	public void create(Session session) {
		loggers.put(session.getId(), LoggerFactory.getLogger(session.getId()));
	}

	public void remove(Session session) {
		loggers.remove(session.getId());
	}

	public void trace(String sessionId, String message, Object... args) {
		if (loggers.containsKey(sessionId)) {
			loggers.get(sessionId).trace(message, args);
		}
	}

	public void debug(String sessionId, String message, Object... args) {
		if (loggers.containsKey(sessionId)) {
			loggers.get(sessionId).debug(message, args);
		}
	}

	public void info(String sessionId, String message, Object... args) {
		if (loggers.containsKey(sessionId)) {
			loggers.get(sessionId).info(message, args);
		}
	}
}
