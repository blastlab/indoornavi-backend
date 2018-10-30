package co.blastlab.serviceblbnavi.rest.facade.report;

import co.blastlab.serviceblbnavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.serviceblbnavi.dto.report.ReportFilterDto;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import co.blastlab.serviceblbnavi.socket.area.AreaEventController;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.google.common.collect.Range;
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
		Range<LocalDateTime> range = Range.openClosed(from, to);
		return coordinatesRepository.findByFloorIdAndInDateRange(filter.getFloorId(), from, to)
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
