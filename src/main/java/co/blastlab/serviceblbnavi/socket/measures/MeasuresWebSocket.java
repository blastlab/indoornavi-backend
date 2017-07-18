package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.serviceblbnavi.socket.bridge.UnrecognizedDeviceException;
import co.blastlab.serviceblbnavi.socket.wizard.SinkDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

	@OnOpen
	public void open(Session session) {
		super.open(session);
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
			broadCastMessage(serverSessions, message);
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
				Optional<Point> coords = coordinatesCalculator.calculateTagPosition(distanceMessage.getDid1(), distanceMessage.getDid2(), distanceMessage.getDist());

				coords.ifPresent(this::saveAndSendCoordinates);
			}
		});
	}

	private boolean bothDevicesAreAnchors(DistanceMessage distanceMessage) {
		return distanceMessage.getDid1() > Short.MAX_VALUE && distanceMessage.getDid2() > Short.MAX_VALUE;
	}

	private void saveAndSendCoordinates(Point point) {
		CoordinatesDto coordinatesDto = new CoordinatesDto("tag", point.getX(), point.getY());
		Coordinates coordinates = new Coordinates();
		coordinates.setDevice(coordinatesDto.getDevice());
		coordinates.setX(coordinatesDto.getX());
		coordinates.setY(coordinatesDto.getY());
		coordinatesRepository.save(coordinates);
		try {
			broadCastMessage(clientSessions, objectMapper.writeValueAsString(coordinatesDto));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
