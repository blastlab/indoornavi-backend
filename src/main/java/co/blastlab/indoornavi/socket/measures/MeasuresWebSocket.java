package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.LoggerController;
import co.blastlab.indoornavi.rest.facade.debug.DebugBean;
import co.blastlab.indoornavi.socket.WebSocket;
import co.blastlab.indoornavi.socket.area.AreaEvent;
import co.blastlab.indoornavi.socket.filters.*;
import co.blastlab.indoornavi.socket.info.controller.NetworkController;
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

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ServerEndpoint("/measures")
@Singleton
public class MeasuresWebSocket extends WebSocket {

	private final static String EMULATOR = "emulator";

	private static Set<Session> frontendSessions = new HashSet<>();
	private static Set<Session> sinkSessions = new HashSet<>();
	private static Set<Session> emulatorSessions = new HashSet<>();
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = new HashMap<>();

	@Inject
	private ObjectMapper objectMapper;

	@Setter
	@Getter
	private boolean isDebugMode;

	@Inject
	private Event<DistanceMessage> distanceMessageEvent;

	//	@Inject
//	private LoggerController logger;
	private Logger logger = LoggerFactory.getLogger("TEST");

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Resource
	private ManagedExecutorService managedExecutorService;

	@Inject
	private Instance<CalculateMeasuresTask> task;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private AnchorRepository anchorRepositoryFake;

	@Inject
	private NetworkController networkController;

	private Map<FilterType, Filter> activeFilters = new HashMap<>();

	private boolean isEmulatorSession(Session session) {
		return session.getRequestParameterMap().containsKey(EMULATOR);
	}

	@OnOpen
	public void open(Session session) {
//		logger.create(session);

		super.open(session, () -> {
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			TagsWrapper tagsWrapper = new TagsWrapper(tags);
			broadCastMessage(frontendSessions, tagsWrapper);

			activeFilters.putIfAbsent(FilterType.FLOOR, new FloorFilter());
			activeFilters.putIfAbsent(FilterType.TAG, new TagFilter());
		}, () -> { });

		if (isEmulatorSession(session)) {
			emulatorSessions.add(session);
			List<Anchor> allWithFloor = anchorRepository.findAllWithFloor();
			List<AnchorDto> anchors = allWithFloor.stream().map(AnchorDto::new).collect(Collectors.toList());
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
		long start = System.nanoTime();
		setSessionThread(session);
		if (isFrontendSession(session)) {
			Command command = objectMapper.readValue(message, Command.class);
			logger.trace(getSessionId(), "Received command: {}", command);
			if (Command.Type.TOGGLE_TAG.equals(command.getType())) {
				activeFilters.get(FilterType.TAG).update(session, objectMapper.readValue(command.getArgs(), Integer.class));
			} else if (Command.Type.SET_FLOOR.equals(command.getType())) {
				activeFilters.get(FilterType.FLOOR).update(session, objectMapper.readValue(command.getArgs(), Long.class));
			} else if (Command.Type.SET_TAGS.equals(command.getType())) {
				for (Integer tagId : objectMapper.readValue(command.getArgs(), Integer[].class)) {
					activeFilters.get(FilterType.TAG).update(session, tagId);
				}
			}
		} else if (isSinkSession(session) || isEmulatorSession(session)) {
//			long startRead = System.nanoTime();
			List<DistanceMessage> measures = objectMapper.readValue(message, new TypeReference<List<DistanceMessage>>() {});
//			long stopRead = System.nanoTime();
//			long startHandle = System.nanoTime();
			handleMeasures(session, measures);
//			long stopHandle = System.nanoTime();
		}
	}

	private void handleMeasures(List<DistanceMessage> measures) {
//		logger.setId(getSessionId());
//		measures.forEach(distanceMessage -> {
//			if (isDebugMode) {
//				distanceMessageEvent.fire(distanceMessage);
//			}
//			logger.trace("Will analyze distance message: {}", distanceMessage);
//
//			networkController.updateLastTimeUpdated(distanceMessage.getDid1());
//			networkController.updateLastTimeUpdated(distanceMessage.getDid2());
//
//			if (bothDevicesAreAnchors(distanceMessage)) {
//				try {
//					logger.trace("Distance message is about two anchors. Transfering it to wizard bridges.");
//					sinkAnchorsDistanceBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
//					anchorPositionBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
//				} catch (UnrecognizedDeviceException unrecognizedDevice) {
//					unrecognizedDevice.printStackTrace();
//				}
//			} else {
//				logger.trace("Trying to calculate coordinates");
//				Optional<UwbCoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
//				coords.ifPresent(coordinatesDto -> {
//					if (isDebugMode) {
//						coordinatesDtoEvent.fire(coordinatesDto);
//					}
//					this.saveCoordinates(coordinatesDto, distanceMessage.getTime());
//					Set<Session> sessions = this.filterSessions(coordinatesDto);
//					broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
//					this.sendAreaEvents(coordinatesDto);
//				});
//			}
//		});
	}

	@Asynchronous
	public void onAreaEventListGenerated(@Observes List<AreaEvent> areaEvents) {
		for (AreaEvent event : areaEvents) {
			broadCastMessage(this.getFrontendSessions(), new AreaEventWrapper(event));
		}
	}

	/**
	 * distanceMessage is fired to
	 *
	 * @see DebugBean#rawMeasureEndpoint
	 * coordinatesDto is fired to
	 * @see DatabaseExecutor#afterCalculationDone
	 * and
	 * @see DebugBean#calculatedCoordinatesEndpoint
	 */
	private void handleMeasures(Session session, List<DistanceMessage> measures) {
		logger.debug("Measures: {}", measures.size());
		CalculateMeasuresTask task = this.task.get();
		task.setSession(session);
		task.setMeasures(measures);
		task.setActiveFilters(this.activeFilters);
		task.setFrontendSessions(getFrontendSessions());
		managedExecutorService.submit(task);
//		measures.forEach(distanceMessage -> {
//			if (isDebugMode) {
//				distanceMessageEvent.fire(distanceMessage);
//			}
////			logger.trace("TEST Will analyze distance message: {}", distanceMessage);
//			logger.debug("TEST Time diff: {}", Math.abs(distanceMessage.getTime().getTime() - new Date().getTime()));
////			long start = System.nanoTime();
//			Optional<UwbCoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(session, distanceMessage);
////			logger.debug("KAROL ALL {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
////			logger.debug("TEST Coordinates calculated {}", coords.isPresent());
//			coords.ifPresent(coordinatesDto -> {
//				Instant instant = Instant.ofEpochMilli(distanceMessage.getTime().getTime());
//				coordinatesDto.setMeasurementTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
//				coordinatesDtoEvent.fire(coordinatesDto);
//
//				Set<Session> sessions = this.filterSessions(coordinatesDto);
//				broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
//			});
//		});
	}

//	private Set<Session> filterSessions(UwbCoordinatesDto coordinatesDto) {
//		Set<Session> sessions = frontendSessions;
//		for (Map.Entry<FilterType, Filter> filterEntry : activeFilters.entrySet()) {
//			if (FilterType.TAG.equals(filterEntry.getKey())) {
//				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getTagShortId());
//			} else if (FilterType.FLOOR.equals(filterEntry.getKey())) {
//				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getFloorId());
//			}
//		}
//		return sessions;
//	}
}
