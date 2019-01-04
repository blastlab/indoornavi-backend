package co.blastlab.serviceblbnavi.socket.tagTracer;

import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.measures.CoordinatesCalculator;
import co.blastlab.serviceblbnavi.utils.Logger;
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
	private Logger logger;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = Collections.synchronizedMap(new HashMap<>());

	@Override
	protected Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	protected Set<Session> getServerSessions() {
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
		coordinatesCalculator.startTracingTags();
		super.open(session);
	}

	@OnClose
	public void close(Session session) {
		coordinatesCalculator.stopTracingTags();
		super.close(session);
	}

	public void tagTraceAdded(@Observes TagTraceDto tagTrace) throws JsonProcessingException {
		logger.setId(getSessionId()).trace("Tag trace added {}", tagTrace);
		broadCastMessage(getClientSessions(), objectMapper.writeValueAsString(tagTrace));
	}
}
