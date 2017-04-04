package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

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
	public void handleMessage(String message, Session session) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (isClientSession(session)) {
			List<AnchorDto> anchors = new ArrayList<>();
			anchorRepository.findAll().forEach(anchor -> anchors.add(new AnchorDto(anchor)));
			broadCastMessage(clientSessions, objectMapper.writeValueAsString(anchors));
		} else if (isServerSession(session)) {
			AnchorDto anchorDto = objectMapper.readValue(message, AnchorDto.class);
			Anchor anchorEntity = new Anchor();
			anchorEntity.setShortId(anchorDto.getShortId());
			anchorEntity.setLongId(anchorDto.getLongId());
			// TODO: remove when Paweł fix this
			anchorEntity.setX(anchorDto.getX());
			anchorEntity.setY(anchorDto.getY());
			anchorEntity.setVerified(false);
			anchorEntity = anchorRepository.save(anchorEntity);
			broadCastMessage(clientSessions, objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto(anchorEntity))));
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
