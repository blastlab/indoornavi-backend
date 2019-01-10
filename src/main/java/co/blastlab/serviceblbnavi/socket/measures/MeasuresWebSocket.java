package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.serviceblbnavi.domain.UwbCoordinates;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
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
import lombok.Getter;
import lombok.Setter;

import javax.ejb.AccessTimeout;
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
@AccessTimeout(unit = TimeUnit.SECONDS, value = 15)
public class MeasuresWebSocket extends WebSocket {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
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

	@Inject
	private Logger logger;

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
		long sumStartTime = System.nanoTime();
		long sumStartTime2 = System.nanoTime();
		measures.forEach(distanceMessage -> {
			if (isDebugMode) {
				distanceMessageEvent.fire(distanceMessage);
			}
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
				long startTime = System.nanoTime();
				Optional<UwbCoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist(), false);
				long endTime = System.nanoTime();
				logger.trace("___________________________________________________________________________________________________");
				logger.trace("calcualting tag position took {} ms", (endTime - startTime) / 1000000);
				logger.trace("___________________________________________________________________________________________________");
				coords.ifPresent(coordinatesDto -> {
					long startTime2 = System.nanoTime();
					if (isDebugMode) {
						coordinatesDtoEvent.fire(coordinatesDto);
					}
					this.saveCoordinates(coordinatesDto, distanceMessage.getTime());
					Set<Session> sessions = this.filterSessions(coordinatesDto);
					broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
					this.sendAreaEvents(coordinatesDto);
					long endTime2 = System.nanoTime();
					logger.trace("___________________________________________________________________________________________________");
					logger.trace("saving tag position took {} ms", (endTime2 - startTime2) / 1000000);
					logger.trace("___________________________________________________________________________________________________");
				});
			}
		});

		long sumEndTime = System.nanoTime();
		long sumEndTime2 = System.nanoTime();
		logger.trace("___________________________________________________________________________________________________");
		logger.trace("sum time calculating {} ms and sum time saving {} ms", (sumEndTime - sumStartTime) / 1000000, (sumEndTime2 - sumStartTime2) / 1000000);
		logger.trace("___________________________________________________________________________________________________");
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

	private void sendAreaEvents(UwbCoordinatesDto coordinatesDto) {
		List<AreaEvent> events = areaEventController.checkCoordinates(coordinatesDto);
		for (AreaEvent event : events) {
			broadCastMessage(this.getClientSessions(), new AreaEventWrapper(event));
		}
	}
}
