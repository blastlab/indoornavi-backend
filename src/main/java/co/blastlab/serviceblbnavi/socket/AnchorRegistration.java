package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
@ServerEndpoint("/anchors/registration")
public class AnchorRegistration {

	private static Set<Session> anchorSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> tagSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> sinkSessions = Collections.synchronizedSet(new HashSet<Session>());

	@Inject
	private AnchorRepository anchorRepository;

	public static void broadcastNewAnchor(Anchor anchor) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto(anchor))));
	}

	@OnOpen
	public void registerSession(Session session) throws JsonProcessingException {
		String queryString = session.getQueryString();
		ObjectMapper objectMapper = new ObjectMapper();
		if (SessionType.SINK.getName().equals(queryString)) {
			sinkSessions.add(session);
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorSessions.add(session);
			List<AnchorDto> anchors = new ArrayList<>();
			anchorRepository.findAll().forEach((anchor) -> {
				anchors.add(new AnchorDto(anchor));
			});
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(anchors));
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagSessions.add(session);
		}
	}

	@OnClose
	public void unregisterSession(Session session) {
		String queryString = session.getQueryString();
		if (SessionType.SINK.getName().equals(queryString)) {
			sinkSessions.remove(session);
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorSessions.remove(session);
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagSessions.remove(session);
		}
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (Objects.equals(session.getQueryString(), SessionType.SINK.getName())) {
			AnchorDto anchorDto = objectMapper.readValue(message, AnchorDto.class);
			Anchor anchorEntity = new Anchor();
			anchorEntity.setShortId(anchorDto.getShortId());
			anchorEntity.setLongId(anchorDto.getLongId());
			// TODO: remove when Paweł fix this
			anchorEntity.setX(anchorDto.getX());
			anchorEntity.setY(anchorDto.getY());
			anchorEntity.setVerified(false);
			anchorEntity = anchorRepository.save(anchorEntity);
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto(anchorEntity))));
		}
	}

	private static void broadCastMessage(final Set<Session> sessions, final String message) {
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
