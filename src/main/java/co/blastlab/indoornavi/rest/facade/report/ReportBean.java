package co.blastlab.indoornavi.rest.facade.report;

import co.blastlab.indoornavi.dao.repository.PhoneCoordinatesRepository;
import co.blastlab.indoornavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.indoornavi.domain.UwbCoordinates;
import co.blastlab.indoornavi.dto.CoordinatesDto;
import co.blastlab.indoornavi.dto.phone.PhoneCoordinatesDto;
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
	private UwbCoordinatesRepository uwbCoordinatesRepository;

	@Inject
	private PhoneCoordinatesRepository phoneCoordinatesRepository;

	@Inject
	private AreaEventController areaEventController;

	@Override
	@SuppressWarnings("unchecked")
	public List<UwbCoordinatesDto> getUwbCoordinates(ReportFilterDto filter) {
		return (List<UwbCoordinatesDto>)getCoordinates(filter, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PhoneCoordinatesDto> getPhoneCoordinates(ReportFilterDto filter) {
		return (List<PhoneCoordinatesDto>)getCoordinates(filter, true);
	}

	@Override
	public List<AreaEvent> getAreaEvents(ReportFilterDto filter) {
		logger.debug("Trying to retrive area events in date range {} - {}", filter.getFrom(), filter.getTo());
		List<AreaEvent> events = new ArrayList<>();
		List<UwbCoordinatesDto> coordinates = getUwbCoordinates(filter);
		coordinates.forEach(coordinatesDto -> {
			events.addAll(areaEventController.checkCoordinates(coordinatesDto));
		});

		return events;
	}

	private List<? extends CoordinatesDto> getCoordinates(ReportFilterDto filter, boolean isPhone) {
		logger.debug("Trying to retrieve coordinates in date range {} - {}", filter.getFrom(), filter.getTo());
		LocalDateTime from = filter.getFrom() != null ? filter.getFrom() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusYears(50);
		LocalDateTime to = filter.getTo() != null ? filter.getTo() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
		if (from.isAfter(to)) {
			throw new WebApplicationException("Invalid date range: `from` can not be after `to`", HttpStatus.SC_UNPROCESSABLE_ENTITY);
		}
		if (isPhone) {
			return phoneCoordinatesRepository.findByFloorIdAndInDateRange(filter.getFloorId(), from, to)
				.stream().map(PhoneCoordinatesDto::new).collect(Collectors.toList());
		} else {
			List<UwbCoordinates> filteredCoordinates;
			if (filter.getFloorId() != null && filter.getTagsIds().size() > 0) {
				filteredCoordinates = uwbCoordinatesRepository.findByFloorAndTagsAndInDateRange(
					filter.getFloorId(),
					from,
					to,
					filter.getTagsIds()
				);
			} else if (filter.getFloorId() != null) {
				filteredCoordinates = uwbCoordinatesRepository.findByFloorIdAndInDateRange(filter.getFloorId(), from, to);
			} else if (filter.getTagsIds().size() > 0) {
				filteredCoordinates = uwbCoordinatesRepository.findByTagsAndInDateRange(
					from,
					to,
					filter.getTagsIds()
				);
			} else {
				filteredCoordinates = uwbCoordinatesRepository.findByDateRange(from, to);
			}
			return filteredCoordinates.stream().map(UwbCoordinatesDto::new).collect(Collectors.toList());
		}
	}
}
