package co.blastlab.indoornavi.rest.facade.report;

import co.blastlab.indoornavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.indoornavi.dto.report.ReportFilterDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.socket.area.AreaEvent;
import co.blastlab.indoornavi.socket.area.AreaEventController;
import co.blastlab.indoornavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReportBean implements ReportFacade {

	@Inject
	private Logger logger;

	@Inject
	private UwbCoordinatesRepository coordinatesRepository;

	@Inject
	private AreaEventController areaEventController;

	@Override
	public List<UwbCoordinatesDto> getCoordinates(ReportFilterDto filter) {
		logger.debug("Trying to retrive coordinates in date range {} - {}", filter.getFrom(), filter.getTo());
		LocalDateTime from = filter.getFrom() != null ? filter.getFrom() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusYears(50);
		LocalDateTime to = filter.getTo() != null ? filter.getTo() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
		if (from.isAfter(to)) {
			throw new WebApplicationException("Invalid date range: `from` can not be after `to`", HttpStatus.SC_UNPROCESSABLE_ENTITY);
		}
		return filter.getFloorId() == null ?
			coordinatesRepository.findByDateRange(from, to)
				.stream().map(UwbCoordinatesDto::new).collect(Collectors.toList()) :
			coordinatesRepository.findByFloorIdAndInDateRange(filter.getFloorId(), from, to)
			.stream().map(UwbCoordinatesDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AreaEvent> getAreaEvents(ReportFilterDto filter) {
		logger.debug("Trying to retrive area events in date range {} - {}", filter.getFrom(), filter.getTo());
		List<AreaEvent> events = new ArrayList<>();
		List<UwbCoordinatesDto> coordinates = getCoordinates(filter);
		coordinates.forEach(coordinatesDto -> {
			events.addAll(areaEventController.checkCoordinates(coordinatesDto));
		});

		return events;
	}

}
