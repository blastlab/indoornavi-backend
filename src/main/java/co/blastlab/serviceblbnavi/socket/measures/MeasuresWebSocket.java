package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import co.blastlab.serviceblbnavi.socket.area.AreaEventController;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.serviceblbnavi.socket.bridge.UnrecognizedDeviceException;
import co.blastlab.serviceblbnavi.socket.filters.Command;
import co.blastlab.serviceblbnavi.socket.filters.Filter;
import co.blastlab.serviceblbnavi.socket.filters.FilterType;
import co.blastlab.serviceblbnavi.socket.filters.TagFilter;
import co.blastlab.serviceblbnavi.socket.wizard.SinkDetails;
import co.blastlab.serviceblbnavi.socket.wrappers.AnchorsWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.AreaEventWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.CoordinatesWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.TagsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServerEndpoint("/measures")
@Singleton
public class MeasuresWebSocket extends WebSocket {

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
	private SinkRepository sinkRepository;

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
	private AreaEventController areaEventController;

	private Map<FilterType, Filter> activeFilters = new HashMap<>();

	@OnOpen
	public void open(Session session) {
		super.open(session, () -> {
			List<TagDto> tags = tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList());
			TagsWrapper tagsWrapper = new TagsWrapper(tags);
			broadCastMessage(clientSessions, tagsWrapper);
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
			if (FilterType.TAG_ACTIVE.equals(command.getFilterType())) {
				if (!activeFilters.containsKey(FilterType.TAG_ACTIVE)) {
					activeFilters.put(FilterType.TAG_ACTIVE, new TagFilter());
				}
				activeFilters.get(FilterType.TAG_ACTIVE).update(session, objectMapper.readValue(command.getArgs(), Integer.class));
			}
		} else if (isServerSession(session)) {
			DistanceMessageWrapper wrapper = objectMapper.readValue(message, DistanceMessageWrapper.class);
			handleInfo(wrapper);
			handleMeasures(wrapper);
		}
	}

	private void handleInfo(DistanceMessageWrapper wrapper) {
		wrapper.getInfo().forEach(info -> {
			if (info.getCode().equals(2)) {
				try {
					SinkDetails sinkDetails = objectMapper.readValue(info.getArgs(), SinkDetails.class);
					Sink sink = sinkRepository.findOptionalByShortId(sinkDetails.getDid()).orElseGet(Sink::new);
					sink.setShortId(sinkDetails.getDid());
					sink.setLongId(sinkDetails.getEui());
					sinkRepository.save(sink);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void handleMeasures(DistanceMessageWrapper wrapper) {
		wrapper.getMeasures().forEach(distanceMessage -> {
			if (bothDevicesAreAnchors(distanceMessage)) {
				try {
					sinkAnchorsDistanceBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
					anchorPositionBridge.addDistance(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());
				} catch (UnrecognizedDeviceException unrecognizedDevice) {
					unrecognizedDevice.printStackTrace();
				}
			} else {
				Optional<CoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());

				coords.ifPresent(this::saveAndSendCoordinates);
			}
		});
	}

	private boolean bothDevicesAreAnchors(DistanceMessage distanceMessage) {
		return distanceMessage.getDid1() > Short.MAX_VALUE && distanceMessage.getDid2() > Short.MAX_VALUE;
	}

	private void saveAndSendCoordinates(CoordinatesDto coordinatesDto) {
		Coordinates coordinates = new Coordinates();
		coordinates.setDevice("TAG");
		coordinates.setX((double) coordinatesDto.getPoint().getX());
		coordinates.setY((double) coordinatesDto.getPoint().getY());
		coordinatesRepository.save(coordinates);
		Set<Session> sessions = clientSessions;
		for(Filter filter : activeFilters.values()) {
			sessions = filter.filter(sessions, coordinatesDto.getDeviceId());
		}
		broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));

		List<AreaEvent> events = areaEventController.checkCoordinates(coordinatesDto);
		for (AreaEvent event : events) {
			broadCastMessage(sessions, new AreaEventWrapper(event));
		}
	}

	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void cleanMeasureTable() {
		coordinatesCalculator.cleanTables();
	}
}
