package pl.indoornavi.coordinatescalculator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.indoornavi.coordinatescalculator.filters.*;
import pl.indoornavi.coordinatescalculator.models.AnchorsWrapper;
import pl.indoornavi.coordinatescalculator.models.CoordinatesWrapper;
import pl.indoornavi.coordinatescalculator.models.DistanceMessage;
import pl.indoornavi.coordinatescalculator.models.UwbCoordinatesDto;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;
import pl.indoornavi.coordinatescalculator.repositories.CoordinatesRepository;
import pl.indoornavi.coordinatescalculator.shared.CoordinatesCalculator;

import java.io.IOException;
import java.util.*;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private static final String SINK_SESSION_QUERY_STRING = "sink";
    private static final String EMULATOR_SESSION_QUERY_STRING = "emulator";
    private static final String FRONTEND_SESSION_QUERY_STRING = "frontend";
    private Map<String, StringBuffer> payloadBuffer = new HashMap<>();
    private Map<WebSocketSession, Filter> frontendSessions = new HashMap<>();

    @Autowired
    public WebSocketHandler(AnchorRepository anchorRepository,
                            ObjectMapper objectMapper,
                            CoordinatesCalculator coordinatesCalculator, CoordinatesRepository coordinatesRepository) {
        this.anchorRepository = anchorRepository;
        this.objectMapper = objectMapper;
        this.coordinatesCalculator = coordinatesCalculator;
        this.coordinatesRepository = coordinatesRepository;
    }

    private final AnchorRepository anchorRepository;
    private final ObjectMapper objectMapper;
    private final CoordinatesCalculator coordinatesCalculator;
    private final CoordinatesRepository coordinatesRepository;

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("Session connected: {}", session.getUri());

        executeForSpecificSession(session, () -> {
            frontendSessions.put(session, new Filter());
        }, () -> {
            payloadBuffer.put(session.getId(), new StringBuffer());
            try {
                String anchorsJsonString = objectMapper.writeValueAsString(new AnchorsWrapper(anchorRepository.findAll()));
                logger.info(anchorsJsonString);
                session.sendMessage(new TextMessage(anchorsJsonString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, () -> payloadBuffer.put(session.getId(), new StringBuffer()));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        executeForSpecificSession(session, () -> {
            handleFrontendMessage(session, message);
        }, () -> {
            handleMeasuresMessage(session, message);
        }, () -> {
            handleMeasuresMessage(session, message);
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.info(exception.getLocalizedMessage());
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.debug("Session disconnected: {}", session.getUri());
        logger.info(status.getReason());

        executeForSpecificSession(session, () -> {
            frontendSessions.remove(session);
        }, () -> {
            payloadBuffer.remove(session.getId());
        }, () -> {
            payloadBuffer.remove(session.getId());
        });

        super.afterConnectionClosed(session, status);
    }

    @Scheduled(fixedRate = 5000)
    public void saveCoordinatesFromBatch() throws IOException {
        coordinatesRepository.saveStoredCoordinates();
    }

    @Scheduled(fixedRate = 1000)
    public void sendCoordinates() {
        Map<Integer, UwbCoordinatesDto> allCoordinates = coordinatesRepository.getCoordinatesToSend();
        frontendSessions.keySet().forEach(frontendSession -> {
            List<UwbCoordinatesDto> sessionCoordinates = new ArrayList<>();
            allCoordinates.forEach((tagShortId, coordinatesDto) -> {
                if (isSessionAllowedToReceiveCoordinates(frontendSession, coordinatesDto)) {
                    sessionCoordinates.add(coordinatesDto);
                }
            });
            try {
                frontendSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(new CoordinatesWrapper(sessionCoordinates)))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        coordinatesRepository.clearCoordinatesToSend();
    }

    private List<DistanceMessage> parseJsonToDistanceMessages(StringBuffer sessionBuffer) throws IOException {
        return objectMapper.readValue(
                sessionBuffer.toString(), new TypeReference<List<DistanceMessage>>() {
                }
        );
    }

    private void executeForSpecificSession(WebSocketSession session,
                                           Runnable doForFrontend,
                                           Runnable doForEmulator,
                                           Runnable doForSink) {
        Optional.ofNullable(session.getUri()).ifPresent(uri -> {
            String query = uri.getQuery();
            if (query.contains(EMULATOR_SESSION_QUERY_STRING)) {
                doForEmulator.run();
            } else if (query.contains(FRONTEND_SESSION_QUERY_STRING)) {
                doForFrontend.run();
            } else if (query.contains(SINK_SESSION_QUERY_STRING)) {
                doForSink.run();
            }
        });
    }

    private void handleMeasuresMessage(WebSocketSession session, WebSocketMessage message) {
        StringBuffer sessionBuffer = payloadBuffer.get(session.getId());
        if (sessionBuffer != null) {
            sessionBuffer.append(message.getPayload());

            if (message.isLast()) {
                try {
                    List<DistanceMessage> distanceMessages = parseJsonToDistanceMessages(sessionBuffer);
                    distanceMessages.forEach(distanceMessage -> {
                        logger.trace("Time difference between measurement time and time get right before calculation: {}ms", Math.abs(distanceMessage.getTime() - new Date().getTime()));
                        coordinatesCalculator.calculateTagPosition(distanceMessage).ifPresent(coordinatesRepository::addToSave);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                payloadBuffer.put(session.getId(), new StringBuffer());
            }
        }
    }

    private void handleFrontendMessage(WebSocketSession session, WebSocketMessage<String> message) {
        try {
            Command command = objectMapper.readValue(message.getPayload(), Command.class);
            logger.trace("Received command: {}", command);
            if (Command.Type.TOGGLE_TAG.equals(command.getType())) {
                Integer tagShortId = objectMapper.readValue(command.getArgs(), Integer.class);
                List<Integer> tagsShortId = frontendSessions.get(session).getTagsShortId();
                if (tagsShortId.contains(tagShortId)) {
                    tagsShortId.remove(tagShortId);
                } else {
                    tagsShortId.add(tagShortId);
                }
            } else if (Command.Type.SET_FLOOR.equals(command.getType())) {
                frontendSessions.get(session).setFloorId(objectMapper.readValue(command.getArgs(), Long.class));
            } else if (Command.Type.SET_TAGS.equals(command.getType())) {
                frontendSessions.get(session).getTagsShortId().addAll(
                        Arrays.asList(objectMapper.readValue(command.getArgs(), Integer[].class))
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSessionAllowedToReceiveCoordinates(WebSocketSession session, UwbCoordinatesDto coordinatesDto) {
        Filter filter = frontendSessions.get(session);
        if (!filter.getFloorId().equals(coordinatesDto.getFloorId())) {
            return false;
        }
        return filter.getTagsShortId().contains(coordinatesDto.getTagShortId());
    }
}
