package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.DistanceMessage;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@ServerEndpoint("/coordinates")
@Singleton
public class WebSocketServer {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	static int getClients() {
		return clientSessions.size();
	}

	@Inject
	private CoordinatesRepository coordinatesRepository;

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Inject
	private TagRepository tagRepository;

	@OnOpen
	public void open(Session session)  {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			clientSessions.add(session);
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			ObjectMapper objectMapper = new ObjectMapper();
			TagsWrapper tagsWrapper = new TagsWrapper(TypeMessage.TAGS, tags);
			try {
				broadCastMessage(clientSessions, objectMapper.writeValueAsString(tagsWrapper));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			serverSessions.add(session);
		}
	}

	@OnClose
	public void close(Session session) {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			clientSessions.remove(session);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			serverSessions.remove(session);
		}
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			broadCastMessage(serverSessions, message);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			ObjectMapper objectMapper = new ObjectMapper();
			List<DistanceMessage> distanceMessages = objectMapper.readValue(message, new TypeReference<List<DistanceMessage>>(){});
			distanceMessages.forEach(distanceMessage -> {
				Optional<Point> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getD1(), distanceMessage.getD2(), distanceMessage.getDist());
				System.out.println(String.format("Sending message: %s ", message));
				if (coords.isPresent()) {
					Point point = coords.get();
					CoordinatesDto coordinatesDto = new CoordinatesDto("test", point.getX(), point.getY());
					Coordinates coordinates = new Coordinates();
					coordinates.setDevice(coordinatesDto.getDevice());
					coordinates.setX(coordinatesDto.getX());
					coordinates.setY(coordinatesDto.getY());
					coordinatesRepository.save(coordinates);
					try {
						//CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper(TypeMessage.COORDINATES, coordinatesDto);
						//broadCastMessage(clientSessions, objectMapper.writeValueAsString(coordinatesWrapper));
						broadCastMessage(clientSessions, objectMapper.writeValueAsString(coordinatesDto));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private void broadCastMessage(final Set<Session> sessions, final String message) {
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
