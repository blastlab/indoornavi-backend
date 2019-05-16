package co.blastlab.indoornavi.socket.tagTracer;

import co.blastlab.indoornavi.socket.LoggerController;
import co.blastlab.indoornavi.socket.WebSocket;
import co.blastlab.indoornavi.socket.measures.CoordinatesCalculator;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

@ServerEndpoint("/tagTracer")
@Singleton
public class TagTracerWebSocket extends WebSocket {

	@Inject
	private LoggerController logger;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	private static Set<Session> frontendSessions = new HashSet<>();
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = new HashMap<>();

	@Override
	protected Set<Session> getFrontendSessions() {
		return frontendSessions;
	}

	@Override
	protected Set<Session> getSinkSessions() {
		// Tag tracer accepts only client sessions
		throw new NotImplementedException();
	}

	@Override
	protected Map<Long, String> getThreadToSessionMap() {
		return threadIdToSessionId;
	}

	@OnOpen
	public void open(Session session) {
		setSessionThread(session);
		logger.create(session);
		coordinatesCalculator.startTracingTags();
		super.open(session);
	}

	@OnClose
	public void close(Session session) {
		logger.remove(session);
		coordinatesCalculator.stopTracingTags();
		super.close(session);
	}

	public void tagTraceAdded(@Observes TagTraceDto tagTrace) throws JsonProcessingException {
		logger.trace(getSessionId(), "Tag trace added {}", tagTrace);
		broadCastMessage(getFrontendSessions(), objectMapper.writeValueAsString(tagTrace));
	}
}
