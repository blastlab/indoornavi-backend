package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.indoornavi.domain.UwbCoordinates;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.WebSocket;
import co.blastlab.indoornavi.socket.area.AreaEvent;
import co.blastlab.indoornavi.socket.area.AreaEventController;
import co.blastlab.indoornavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.indoornavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.indoornavi.socket.bridge.UnrecognizedDeviceException;
import co.blastlab.indoornavi.socket.filters.*;
import co.blastlab.indoornavi.socket.wrappers.AnchorsWrapper;
import co.blastlab.indoornavi.socket.wrappers.AreaEventWrapper;
import co.blastlab.indoornavi.socket.wrappers.CoordinatesWrapper;
import co.blastlab.indoornavi.socket.wrappers.TagsWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ServerEndpoint("/measures")
@Singleton
public class MeasuresWebSocket extends WebSocket {

	private final static String EMULATOR = "emulator";

	private static Set<Session> frontendSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> sinkSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> emulatorSessions = Collections.synchronizedSet(new HashSet<>());
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = Collections.synchronizedMap(new HashMap<>());

	@Inject
	private ObjectMapper objectMapper;

	@Setter
	@Getter
	private boolean isDebugMode;

	@Inject
	private Event<DistanceMessage> distanceMessageEvent;

	@Inject
	private Event<UwbCoordinatesDto> coordinatesDtoEvent;

//	@Inject
//	private Logger logger;

	@Inject
	private UwbCoordinatesRepository coordinatesRepository;

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

	private static Logger logger = LoggerFactory.getLogger("_________________MyLogger__________________");

	@Inject
	private AreaEventController areaEventController;

	private Map<FilterType, Filter> activeFilters = new HashMap<>();

	private boolean isEmulatorSession(Session session) {
		return session.getRequestParameterMap().containsKey(EMULATOR);
	}

	@OnOpen
	public void open(Session session) {
		super.open(session, () -> {
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			TagsWrapper tagsWrapper = new TagsWrapper(tags);
			broadCastMessage(frontendSessions, tagsWrapper);

			activeFilters.putIfAbsent(FilterType.FLOOR, new FloorFilter());
			activeFilters.putIfAbsent(FilterType.TAG, new TagFilter());
		}, () -> { });

		if (isEmulatorSession(session)) {
			emulatorSessions.add(session);
			List<AnchorDto> anchors = anchorRepository.findAllWithFloor().stream().map(AnchorDto::new).collect(Collectors.toList());
			broadCastMessage(emulatorSessions, new AnchorsWrapper(anchors));
		}
	}

	@OnClose
	public void close(Session session) {
		super.close(session);
		emulatorSessions.remove(session);
	}

	@Override
	protected Set<Session> getFrontendSessions() {
		return frontendSessions;
	}

	@Override
	protected Set<Session> getSinkSessions() {
		return sinkSessions;
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
		if (isFrontendSession(session)) {
			Command command = objectMapper.readValue(message, Command.class);
//			logger.setId(getSessionId()).trace("Received command: {}", command);
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
		} else if (isSinkSession(session) || isEmulatorSession(session)) {
			List<DistanceMessage> measures = objectMapper.readValue(message, new TypeReference<List<DistanceMessage>>(){});
			handleMeasures(measures);
		}
	}

	private void handleMeasures(List<DistanceMessage> measures) {
//		logger.setId(getSessionId());
		measures.forEach(distanceMessage -> {
			if (isDebugMode) {
				distanceMessageEvent.fire(distanceMessage);
			}
//			logger.trace("Will analyze distance message: {}", distanceMessage);
			if (bothDevicesAreAnchors(distanceMessage)) {
				try {
//					logger.trace("Distance message is about two anchors. Transfering it to wizard bridges.");
					sinkAnchorsDistanceBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
					anchorPositionBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
				} catch (UnrecognizedDeviceException unrecognizedDevice) {
					unrecognizedDevice.printStackTrace();
				}
			} else {
//				logger.trace("Trying to calculate coordinates");
//				long start = System.nanoTime();
				logger.debug("Time diff: {}", Math.abs(distanceMessage.getTime().getTime() - new Date().getTime()));
				Optional<UwbCoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
//				logger.debug("______________________________________)__)_)_)_)_)_)_)_)_)_ {} microseconds", TimeUnit.MICROSECONDS.convert((System.nanoTime() - start), TimeUnit.NANOSECONDS));
				coords.ifPresent(coordinatesDto -> {
					if (isDebugMode) {
						coordinatesDtoEvent.fire(coordinatesDto);
					}
					this.saveCoordinates(coordinatesDto, distanceMessage.getTime());
					Set<Session> sessions = this.filterSessions(coordinatesDto);
					broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
					this.sendAreaEvents(coordinatesDto);
				});
			}
		});
	}

	private boolean bothDevicesAreAnchors(DistanceMessage distanceMessage) {
		return distanceMessage.getDid1() > Short.MAX_VALUE && distanceMessage.getDid2() > Short.MAX_VALUE;
	}

	private void saveCoordinates(UwbCoordinatesDto coordinatesDto, Timestamp timestamp) {
		UwbCoordinates coordinates = new UwbCoordinates();
		coordinates.setTag(tagRepository.findOptionalByShortId(coordinatesDto.getTagShortId()).orElseThrow(EntityNotFoundException::new));
		coordinates.setX(coordinatesDto.getPoint().getX());
		coordinates.setY(coordinatesDto.getPoint().getY());
		coordinates.setZ(coordinatesDto.getPoint().getZ());
		coordinates.setFloor(floorRepository.findOptionalById(coordinatesDto.getFloorId()).orElseThrow(EntityNotFoundException::new));
		Instant instant = Instant.ofEpochMilli(timestamp.getTime());
		coordinates.setMeasurementTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
		coordinatesRepository.save(coordinates);
	}

	private Set<Session> filterSessions(UwbCoordinatesDto coordinatesDto) {
		Set<Session> sessions = frontendSessions;
		for(Map.Entry<FilterType, Filter> filterEntry : activeFilters.entrySet()) {
			if (FilterType.TAG.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getTagShortId());
			} else if (FilterType.FLOOR.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getFloorId());
			}
		}
		return sessions;
	}

	private void sendAreaEvents(UwbCoordinatesDto coordinatesDto) {
		List<AreaEvent> events = areaEventController.checkCoordinates(coordinatesDto);
		for (AreaEvent event : events) {
			broadCastMessage(this.getFrontendSessions(), new AreaEventWrapper(event));
		}
	}
}
