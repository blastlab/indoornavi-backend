package pl.indoornavi.coordinatescalculator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import pl.indoornavi.coordinatescalculator.filters.Command;
import pl.indoornavi.coordinatescalculator.filters.Filter;
import pl.indoornavi.coordinatescalculator.models.AnchorsWrapper;
import pl.indoornavi.coordinatescalculator.models.CoordinatesWrapper;
import pl.indoornavi.coordinatescalculator.models.DistanceMessage;
import pl.indoornavi.coordinatescalculator.models.UwbCoordinates;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;
import pl.indoornavi.coordinatescalculator.repositories.CoordinatesRepository;
import pl.indoornavi.coordinatescalculator.services.CoordinatesService;
import pl.indoornavi.coordinatescalculator.shared.CoordinatesCalculator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private static final String SINK_SESSION_QUERY_STRING = "sink";
    private static final String EMULATOR_SESSION_QUERY_STRING = "emulator";
    private static final String FRONTEND_SESSION_QUERY_STRING = "frontend";
    private Map<String, StringBuilder> payloadBuffer = new HashMap<>();
    private Map<WebSocketSession, Filter> frontendSessions = new HashMap<>();

    @Autowired
    public WebSocketHandler(AnchorRepository anchorRepository,
                            ObjectMapper objectMapper,
                            CoordinatesCalculator coordinatesCalculator,
                            CoordinatesRepository coordinatesRepository,
                            CoordinatesService coordinatesService) {
        this.anchorRepository = anchorRepository;
        this.objectMapper = objectMapper;
        this.coordinatesCalculator = coordinatesCalculator;
        this.coordinatesRepository = coordinatesRepository;
        this.coordinatesService = coordinatesService;
    }

    private final AnchorRepository anchorRepository;
    private final ObjectMapper objectMapper;
    private final CoordinatesCalculator coordinatesCalculator;
    private final CoordinatesRepository coordinatesRepository;
    private final CoordinatesService coordinatesService;

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
            payloadBuffer.put(session.getId(), new StringBuilder());
            try {
                String anchorsJsonString = objectMapper.writeValueAsString(new AnchorsWrapper(anchorRepository.findAll()));
                logger.debug(anchorsJsonString);
                session.sendMessage(new TextMessage(anchorsJsonString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, () -> payloadBuffer.put(session.getId(), new StringBuilder()));

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
        logger.debug(exception.getLocalizedMessage());
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.debug("Session disconnected: {}", session.getUri());
        logger.debug(status.getReason());

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

    @Scheduled(fixedDelay = 1000)
    public void sendCoordinates() {
        List<UwbCoordinates> allCoordinates = coordinatesService.getCoordinatesToSend();
        frontendSessions.keySet().forEach(frontendSession -> {
            List<UwbCoordinates> sessionCoordinates = new ArrayList<>();
            allCoordinates.forEach((coordinatesDto) -> {
                if (isSessionAllowedToReceiveCoordinates(frontendSession, coordinatesDto)) {
                    sessionCoordinates.add(coordinatesDto);
                }
            });
            try {
                frontendSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(new CoordinatesWrapper(sessionCoordinates)))
                );
            } catch (IOException e) {
                logger.info(e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }

    private List<DistanceMessage> parseJsonToDistanceMessages(StringBuilder sessionBuffer) throws IOException {
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

    private Map<Long, List<Long>> timeDifferencesPerThread = new ConcurrentHashMap<>();

    private void handleMeasuresMessage(WebSocketSession session, WebSocketMessage message) {
        StringBuilder sessionBuffer = payloadBuffer.get(session.getId());
        if (sessionBuffer != null) {
            sessionBuffer.append(message.getPayload());

            if (message.isLast()) {
                try {
                    List<DistanceMessage> distanceMessages = parseJsonToDistanceMessages(sessionBuffer);
                    distanceMessages.forEach(distanceMessage -> {
                        addTime(distanceMessage.getTime());
                        coordinatesCalculator.calculateTagPosition(distanceMessage).ifPresent(coordinatesService::add);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                payloadBuffer.put(session.getId(), new StringBuilder());
                displayTime();
                clearTime();
            }
        }
    }

    private void handleFrontendMessage(WebSocketSession session, WebSocketMessage<String> message) {
        try {
            Command command = objectMapper.readValue(message.getPayload(), Command.class);
            if (logger.isTraceEnabled()) {
                logger.trace("Received command: {}", command);
            }
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

    private boolean isSessionAllowedToReceiveCoordinates(WebSocketSession session, UwbCoordinates coordinatesDto) {
        Filter filter = frontendSessions.get(session);
        if (!filter.getFloorId().equals(coordinatesDto.getFloorId())) {
            return false;
        }
        return filter.getTagsShortId().contains(coordinatesDto.getTagId());
    }

    private void addTime(Long time) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        List<Long> times = timeDifferencesPerThread.get(Thread.currentThread().getId());
        if (times != null) {
            timeDifferencesPerThread.get(Thread.currentThread().getId()).add(Math.abs(time - new Date().getTime()));
        } else {
            timeDifferencesPerThread.put(Thread.currentThread().getId(), Lists.newArrayList(Math.abs(time - new Date().getTime())));
        }
    }

    private void clearTime() {
        if (!logger.isDebugEnabled()) {
            return;
        }
        timeDifferencesPerThread.remove(Thread.currentThread().getId());
    }

    private void displayTime() {
        if (!logger.isDebugEnabled()) {
            return;
        }
        List<Long> times = timeDifferencesPerThread.get(Thread.currentThread().getId());
        if (times != null) {
            logger.debug("Time difference between measurement time and time get right before calculation: {}ms",
                    timeDifferencesPerThread.get(Thread.currentThread().getId()).stream().mapToLong(t -> t).average().orElse(0L)
            );
        }
    }
}
