package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.indoornavi.domain.UwbCoordinates;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.socket.area.AreaEvent;
import co.blastlab.indoornavi.socket.area.AreaEventController;
import co.blastlab.indoornavi.socket.wrappers.AreaEventWrapper;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Stateless
public class DatabaseExecutor {
	@Inject
	private TagRepository tagRepository;
	@Inject
	private FloorRepository floorRepository;
	@Inject
	private UwbCoordinatesRepository uwbCoordinatesRepository;
	@Inject
	private AreaEventController areaEventController;
	@Inject
	private Event<List<AreaEvent>> areaEventSender;

	public void afterCalculationDone(@Observes UwbCoordinatesDto uwbCoordinatesDto) {
		this.saveCoordinates(uwbCoordinatesDto);
		this.sendAreaEvents(uwbCoordinatesDto);
	}

	private void saveCoordinates(UwbCoordinatesDto coordinatesDto) {
		UwbCoordinates coordinates = new UwbCoordinates();
		coordinates.setTag(tagRepository.findOptionalByShortId(coordinatesDto.getTagShortId()).orElseThrow(EntityNotFoundException::new));
		coordinates.setX(coordinatesDto.getPoint().getX());
		coordinates.setY(coordinatesDto.getPoint().getY());
		coordinates.setZ(coordinatesDto.getPoint().getZ());
		coordinates.setFloor(floorRepository.findOptionalById(coordinatesDto.getFloorId()).orElseThrow(EntityNotFoundException::new));
//		Instant instant = Instant.ofEpochMilli(timestamp.getTime());
//		coordinates.setMeasurementTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
		uwbCoordinatesRepository.save(coordinates);
	}

	private void sendAreaEvents(UwbCoordinatesDto coordinatesDto) {
		List<AreaEvent> events = areaEventController.checkCoordinates(coordinatesDto);
		areaEventSender.fire(events);
	}
}
