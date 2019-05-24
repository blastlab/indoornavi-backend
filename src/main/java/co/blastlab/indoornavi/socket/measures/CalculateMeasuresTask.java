package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dto.CoordinatesDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.socket.WebSocketCommunication;
import co.blastlab.indoornavi.socket.filters.Filter;
import co.blastlab.indoornavi.socket.filters.FilterType;
import co.blastlab.indoornavi.socket.wrappers.CoordinatesWrapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.Session;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;

public class CalculateMeasuresTask extends WebSocketCommunication implements Callable<Void> {
	@Setter
	private List<DistanceMessage> measures;
	private Logger logger = LoggerFactory.getLogger(getClass().getName());
	@Setter
	private Session session;
	@Inject
	private CoordinatesCalculator coordinatesCalculator;
	@Inject
	private Event<UwbCoordinatesDto> coordinatesDtoEvent;
	@Setter
	private Map<FilterType, Filter> activeFilters = new HashMap<>();
	@Setter
	private Set<Session> frontendSessions = new HashSet<>();

	public CalculateMeasuresTask() {
	}

	@Override
	public Void call() throws Exception {
		measures.forEach(distanceMessage -> {
//			if (isDebugMode) {
//				distanceMessageEvent.fire(distanceMessage);
//			}
//			logger.trace("TEST Will analyze distance message: {}", distanceMessage);
			logger.debug("TEST Time diff: {}", Math.abs(distanceMessage.getTime().getTime() - new Date().getTime()));
			coordinatesCalculator.calculateTagPosition(session, distanceMessage).ifPresent(coordinatesDto -> {
				Instant instant = Instant.ofEpochMilli(distanceMessage.getTime().getTime());
				coordinatesDto.setMeasurementTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
				coordinatesDtoEvent.fire(coordinatesDto);

				Set<Session> sessions = this.filterSessions(coordinatesDto);
				broadCastMessage(sessions, new CoordinatesWrapper(coordinatesDto));
			});
		});
		return null;
	}

	private Set<Session> filterSessions(UwbCoordinatesDto coordinatesDto) {
		Set<Session> sessions = this.frontendSessions;
		for (Map.Entry<FilterType, Filter> filterEntry : activeFilters.entrySet()) {
			if (FilterType.TAG.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getTagShortId());
			} else if (FilterType.FLOOR.equals(filterEntry.getKey())) {
				sessions = filterEntry.getValue().filter(sessions, coordinatesDto.getFloorId());
			}
		}
		return sessions;
	}
}
