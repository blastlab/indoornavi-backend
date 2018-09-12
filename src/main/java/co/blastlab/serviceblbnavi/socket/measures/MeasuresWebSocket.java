package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import co.blastlab.serviceblbnavi.socket.area.AreaEventController;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.serviceblbnavi.socket.bridge.UnrecognizedDeviceException;
import co.blastlab.serviceblbnavi.socket.filters.*;
import co.blastlab.serviceblbnavi.socket.wrappers.AnchorsWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.AreaEventWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.CoordinatesWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.TagsWrapper;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServerEndpoint("/measures")
@Singleton
public class MeasuresWebSocket extends WebSocket {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = Collections.synchronizedMap(new HashMap<>());

	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
	}

	@Inject
	private Logger logger;

	@Inject
	private CoordinatesRepository coordinatesRepository;

	@Inject
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceBridge;

	@Inject
	private AnchorPositionBridge anchorPositionBridge;

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private AreaEventController areaEventController;

	private Map<FilterType, Filter> activeFilters = new HashMap<>();

	@OnOpen
	public void open(Session session) {
		super.open(session, () -> {
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			TagsWrapper tagsWrapper = new TagsWrapper(tags);
			broadCastMessage(clientSessions, tagsWrapper);

			activeFilters.putIfAbsent(FilterType.FLOOR, new FloorFilter());
			activeFilters.putIfAbsent(FilterType.TAG, new TagFilter());
		}, () -> {
			List<AnchorDto> anchors = anchorRepository.findAll().stream().map(AnchorDto::new).collect(Collectors.toList());
			broadCastMessage(serverSessions, new AnchorsWrapper(anchors));
		});
	}

	@OnClose
	public void close(Session session) {
		super.close(session);
	}

	@Override
	protected Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	protected Set<Session> getServerSessions() {
		return serverSessions;
	}

	@Override
	protected Map<Long, String> getThreadToSessionMap() {
		return threadIdToSessionId;
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		setSessionThread(session);
		if (isClientSession(session)) {
			Command command = objectMapper.readValue(message, Command.class);
			logger.setId(getSessionId()).trace("Received command: {}", command);
			if (Command.Type.TOGGLE_TAG.equals(command.getType())) {
				activeFilters.get(FilterType.TAG).update(session, objectMapper.readValue(command.getArgs(), Integer.class));
			}
			else if (Command.Type.SET_FLOOR.equals(command.getType())) {
				activeFilters.get(FilterType.FLOOR).update(session, objectMapper.readValue(command.getArgs(), Long.class));
			}
			else if (Command.Type.SET_TAGS.equals(command.getType())) {
				for (Integer tagId : objectMapper.readValue(command.getArgs(), Integer[].class)) {
					activeFilters.get(FilterType.TAG).update(session, tagId);
				}
			}
		} else if (isServerSession(session)) {
			List<DistanceMessage> measures = objectMapper.readValue(message, new TypeReference<List<DistanceMessage>>(){});
			handleMeasures(measures);
		}
	}

	private void handleMeasures(List<DistanceMessage> measures) {
		logger.setId(getSessionId());
		measures.forEach(distanceMessage -> {
			logger.trace("Will analyze distance message: {}", distanceMessage);
			if (bothDevicesAreAnchors(distanceMessage)) {
				try {
					logger.trace("Distance message is about two anchors. Transfering it to wizard bridges.");
					sinkAnchorsDistanceBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
					anchorPositionBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
				} catch (UnrecognizedDeviceException unrecognizedDevice) {
					unrecognizedDevice.printStackTrace();
				}
			} else {
				logger.trace("Trying to calculate coordinates");
				Optional<CoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist(), true);
				if (coords.isPresent()) {
					this.saveCoordinates(coords.get());
					Set<Session> sessions = this.filterSessions(coords.get());
					broadCastMessage(sessions, new CoordinatesWrapper(coords.get()));
					this.sendAreaEvents(coords.get());
				}
			}
		});
	}

	private boolean bothDevicesAreAnchors(DistanceMessage distanceMessage) {
		return distanceMessage.getDid1() > Short.MAX_VALUE && distanceMessage.getDid2() > Short.MAX_VALUE;
	}

	private void saveCoordinates(CoordinatesDto coordinatesDto) {
		Coordinates coordinates = new Coordinates();
		coordinates.setTag(tagRepository.findOptionalByShortId(coordinatesDto.getTagShortId()).orElseThrow(EntityNotFoundException::new));
		coordinates.setX(coordinatesDto.getPoint().getX());
		coordinates.setY(coordinatesDto.getPoint().getY());
		coordinates.setFloor(floorRepository.findOptionalById(coordinatesDto.getFloorId()).orElseThrow(EntityNotFoundException::new));
		coordinatesRepository.save(coordinates);
	}

	private Set<Session> filterSessions(CoordinatesDto coordinatesDto) {
		Set<Session> sessions = clientSessions;
		for(Map.Entry<FilterType, Filter> filterEntry : activeFilters.entrySet()) {
			if (FilterType.TAG.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getTagShortId());
			} else if (FilterType.FLOOR.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getFloorId());
			}
		}
		return sessions;
	}

	private void sendAreaEvents(CoordinatesDto coordinatesDto) {
		List<AreaEvent> events = areaEventController.checkCoordinates(coordinatesDto);
		for (AreaEvent event : events) {
			broadCastMessage(this.getClientSessions(), new AreaEventWrapper(event));
		}
	}
}
