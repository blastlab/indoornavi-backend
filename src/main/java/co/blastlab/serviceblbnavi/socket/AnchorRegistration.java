package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@ServerEndpoint("/anchors/registration")
public class AnchorRegistration extends WebSocketServerAbstract {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

	@Inject
	private AnchorRepository anchorRepository;

	@OnOpen
	public void registerSession(Session session) {
		super.registerSession(session);
	}

	@OnClose
	public void unregisterSession(Session session) {
		super.unregisterSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws JsonProcessingException {
		if (isClientSession(session)) {
			List<Anchor> anchorList = anchorRepository.findByVerified(true);
			ObjectMapper objectMapper = new ObjectMapper();
			broadCastMessage(clientSessions, objectMapper.writeValueAsString(anchorList));
		} else if (isServerSession(session)) {
			// register new anchor and broadcast message to clients
		}
	}

	@Override
	Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	Set<Session> getServerSessions() {
		return serverSessions;
	}
}
