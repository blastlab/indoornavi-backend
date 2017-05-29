package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.DistanceMessage;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.socket.dto.AnchorsWrapper;
import co.blastlab.serviceblbnavi.socket.dto.CoordinatesWrapper;
import co.blastlab.serviceblbnavi.socket.dto.MessageWrapper;
import co.blastlab.serviceblbnavi.socket.dto.TagsWrapper;
import co.blastlab.serviceblbnavi.socket.filters.Command;
import co.blastlab.serviceblbnavi.socket.filters.Filter;
import co.blastlab.serviceblbnavi.socket.filters.FilterType;
import co.blastlab.serviceblbnavi.socket.filters.TagFilter;
import co.blastlab.serviceblbnavi.socket.utils.CoordinatesCalculator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServerEndpoint("/coordinates")
@Singleton
public class WebSocketServer {

	private static Set<Session> clientSessions = new HashSet<>();
	private static Set<Session> serverSessions = new HashSet<>();

	private final static String CLIENT = "client";
	private final static String SERVER = "server";

	@Inject
	private CoordinatesRepository coordinatesRepository;

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private AnchorRepository anchorRepository;

	private Map<FilterType, Filter> activeFilters = new HashMap<>();

	@OnOpen
	public void open(Session session)  {
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			clientSessions.add(session);
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			TagsWrapper tagsWrapper = new TagsWrapper(tags);
			broadCastMessage(clientSessions, tagsWrapper);
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			serverSessions.add(session);
			List<AnchorDto> anchors = anchorRepository.findAll().stream().map(AnchorDto::new).collect(Collectors.toList());
			broadCastMessage(serverSessions, new AnchorsWrapper(anchors));
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
		ObjectMapper objectMapper = new ObjectMapper();
		if (Objects.equals(session.getQueryString(), CLIENT)) {
			Command command = objectMapper.readValue(message, Command.class);
			if (FilterType.TAG_ACTIVE.equals(command.getFilterType())) {
				if (!activeFilters.containsKey(FilterType.TAG_ACTIVE)) {
					activeFilters.put(FilterType.TAG_ACTIVE, new TagFilter());
				}
				activeFilters.get(FilterType.TAG_ACTIVE).update(session, objectMapper.readValue(command.getArgs(), Integer.class));
			}
		} else if (Objects.equals(session.getQueryString(), SERVER)) {
			List<DistanceMessage> distanceMessages = objectMapper.readValue(message, new TypeReference<List<DistanceMessage>>(){});
			distanceMessages.forEach(distanceMessage -> {
				Optional<CoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
				if (coords.isPresent()) {
					CoordinatesDto coordinatesDto = coords.get();
					Coordinates coordinates = new Coordinates();
					coordinates.setDevice("TAG");
					coordinates.setX(Double.valueOf(coordinatesDto.getPoint().getX()));
					coordinates.setY(Double.valueOf(coordinatesDto.getPoint().getY()));
					coordinatesRepository.save(coordinates);
					Set<Session> sessions = clientSessions;
					for(Filter filter : activeFilters.values()) {
						sessions = filter.filter(sessions, coords.get().getDeviceId());
					}
					broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
				}
			});
		}
	}

	private void broadCastMessage(final Set<Session> sessions, final MessageWrapper message) {
		ObjectMapper objectMapper = new ObjectMapper();
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void cleanMeasureTable() {
		coordinatesCalculator.cleanMeasureTable();
	}
}
