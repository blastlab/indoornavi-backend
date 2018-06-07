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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
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

	private static Logger LOGGER = LoggerFactory.getLogger(MeasuresWebSocket.class);

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
	}

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

	@Resource
	private ManagedExecutorService managedExecutorService;

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

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (isClientSession(session)) {
			Command command = objectMapper.readValue(message, Command.class);
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
		measures.forEach(distanceMessage -> {
			if (bothDevicesAreAnchors(distanceMessage)) {
				try {
					sinkAnchorsDistanceBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
					anchorPositionBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
				} catch (UnrecognizedDeviceException unrecognizedDevice) {
					unrecognizedDevice.printStackTrace();
				}
			} else {
				Optional<CoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
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

	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void cleanMeasureTable() {
		LOGGER.trace("Checking if there are any old measures in table and cleaning it.");
		managedExecutorService.execute(() -> coordinatesCalculator.cleanTables());
	}
}
