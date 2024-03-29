package co.blastlab.indoornavi.socket.wizard;

import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.sink.SinkDto;
import co.blastlab.indoornavi.socket.WebSocketCommunication;
import co.blastlab.indoornavi.socket.bridge.AnchorDistance;
import co.blastlab.indoornavi.socket.bridge.AnchorPoints;
import co.blastlab.indoornavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.indoornavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ServerEndpoint("/wizard")
@Singleton
public class WizardWebSocket extends WebSocketCommunication {

	private static Session session;

	private Integer sinkShortId;

	private Long floorId;

	private Integer firstAnchorShortId;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceController;

	@Inject
	private AnchorPositionBridge anchorPositionCalculator;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private Logger logger;

	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		this.objectMapper = new ObjectMapper();
	}

	@OnOpen
	public void open(Session sessionToOpen) throws JsonProcessingException {
		if (session == null) {
			session = sessionToOpen;

			logger.setId(sessionToOpen.getId()).trace("Wizard websocket opened");

			List<Sink> sinks = sinkRepository.findByConfigured(false);
			broadCastMessage(Collections.singleton(sessionToOpen), objectMapper.writeValueAsString(sinks.stream().map(SinkDto::new).collect(toList())));
		}
	}

	@OnClose
	public void close(Session sessionToClose) {
		if (sessionToClose.equals(session)) {

			logger.setId(sessionToClose.getId()).trace("Wizard websocket closed");

			session = null;
			if (this.sinkShortId != null) {
				sinkAnchorsDistanceController.stopListening(this.sinkShortId);
				if (this.firstAnchorShortId != null) {
					anchorPositionCalculator.stopListening(this.sinkShortId, this.firstAnchorShortId);
					this.firstAnchorShortId = null;
				}
				this.sinkShortId = null;
			}
		}
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void anchorDistanceAdded(@Observes AnchorDistance anchorDistance) throws JsonProcessingException {
		broadCastMessage(Collections.singleton(session), objectMapper.writeValueAsString(anchorDistance));
	}

	public void anchorPointsCalculated(@Observes AnchorPoints points) throws JsonProcessingException {
		broadCastMessage(Collections.singleton(session), objectMapper.writeValueAsString(points));
	}

	@OnMessage
	public void handleMessage(String message, Session arrivedSession) throws IOException {
		if (arrivedSession.equals(session)) {
			logger.setId(arrivedSession.getId()).trace("Received step: {}", message);

			WizardStep wizardStep = objectMapper.readValue(message, WizardStep.class);

			if (wizardStep.getStep().equals(Step.FIRST)) {
				FirstStep firstStep = objectMapper.readValue(message, FirstStep.class);
				this.sinkShortId = firstStep.getSinkShortId();
				this.floorId = firstStep.getFloorId();
				logger.trace("Start listening to sink to anchors' distances");
				sinkAnchorsDistanceController.startListening(this.sinkShortId);
			} else if (wizardStep.getStep().equals(Step.SECOND)) {
				SecondStep secondStep = objectMapper.readValue(message, SecondStep.class);
				this.firstAnchorShortId = secondStep.getAnchorShortId();
				logger.trace("Start listening to anchors to anchors' distances");
				anchorPositionCalculator.startListening(this.sinkShortId, this.firstAnchorShortId, secondStep.getSinkPosition(), secondStep.getDegree());
			} else if (wizardStep.getStep().equals(Step.THIRD)) {
				Optional<Sink> sinkOptional = sinkRepository.findOptionalByShortId(this.sinkShortId);
				if (sinkOptional.isPresent()) {
					Sink sink = sinkOptional.get();
					sink.setConfigured(true);
					Optional<Floor> floorOptional = floorRepository.findOptionalById(this.floorId);
					floorOptional.ifPresent(sink::setFloor);
					logger.trace("Wizard completed. Saving sink");
					sinkRepository.save(sink);
				}
			}
		}

	}

	public static void broadcastNewSink(Sink sink) throws JsonProcessingException {
		if (session != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			broadCastMessage(Collections.singleton(session), objectMapper.writeValueAsString(Collections.singletonList(new SinkDto(sink))));
		}
	}
}
