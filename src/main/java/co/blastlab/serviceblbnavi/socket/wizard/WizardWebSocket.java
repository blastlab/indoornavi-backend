package co.blastlab.serviceblbnavi.socket.wizard;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.floor.Point;
import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorDistance;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
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

import static java.util.stream.Collectors.toList;

@ServerEndpoint("/wizard")
@Singleton
public class WizardWebSocket extends WebSocketCommunication {

	private static Session session;

	private Integer sinkId;

	private Integer firstAnchorId;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceController;

	@Inject
	private AnchorPositionBridge anchorPositionCalculator;

	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		this.objectMapper = new ObjectMapper();
	}

	@OnOpen
	public void open(Session sessionToOpen) throws JsonProcessingException {
		if (session == null) {
			session = sessionToOpen;

			List<Sink> sinks = sinkRepository.findAll();
			broadCastMessage(Collections.singleton(sessionToOpen), objectMapper.writeValueAsString(sinks.stream().map(AnchorDto::new).collect(toList())));
		}
	}

	@OnClose
	public void close(Session sessionToClose) {
		if (sessionToClose.equals(session)) {
			session = null;
			if (this.sinkId != null) {
				sinkAnchorsDistanceController.stopListening(this.sinkId);
				if (this.firstAnchorId != null) {
					anchorPositionCalculator.stopListening(this.sinkId, this.firstAnchorId);
					this.firstAnchorId = null;
				}
				this.sinkId = null;
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

	public void anchorPointsCalculated(@Observes List<Point> points) throws JsonProcessingException {
		broadCastMessage(Collections.singleton(session), objectMapper.writeValueAsString(points));
	}

	@OnMessage
	public void handleMessage(String message, Session arrivedSession) throws IOException {
		if (arrivedSession.equals(session)) {
			WizardStep wizardStep = objectMapper.readValue(message, WizardStep.class);

			if (wizardStep.isFirstStep()) {
				this.sinkId = wizardStep.getSinkShortId();
				sinkAnchorsDistanceController.startListening(wizardStep.getSinkShortId());
			} else if (wizardStep.isSecondStep()) {
				this.firstAnchorId = wizardStep.getAnchorShortId();
				anchorPositionCalculator.startListening(wizardStep.getSinkShortId(), wizardStep.getAnchorShortId(), wizardStep.getSinkPosition(), wizardStep.getDegree());
			}
		}

	}

	public static void broadcastNewSink(Sink sink) throws JsonProcessingException {
		if (session != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			broadCastMessage(Collections.singleton(session), objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto(sink))));
		}
	}
}
