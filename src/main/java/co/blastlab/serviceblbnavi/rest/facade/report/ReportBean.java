package co.blastlab.serviceblbnavi.rest.facade.report;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.UwbCoordinatesRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.dto.report.ReportFilterDto;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.google.common.collect.Range;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class ReportBean implements ReportFacade {

	@Inject
	private Logger logger;

	@Inject
	private UwbCoordinatesRepository coordinatesRepository;

	@Inject
	private AreaRepository areaRepository;

	@Override
	public List<UwbCoordinatesDto> getCoordinates(ReportFilterDto filter) {
		logger.debug("Trying to retrive coordinates in date range {} - {}", filter.getFrom(), filter.getTo());
		LocalDateTime from = filter.getFrom() != null ? filter.getFrom() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusYears(50);
		LocalDateTime to = filter.getTo() != null ? filter.getTo() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
		if (from.isAfter(to)) {
			throw new WebApplicationException("Invalid date range: `from` can not be after `to`", HttpStatus.SC_UNPROCESSABLE_ENTITY);
		}
		Range<LocalDateTime> range = Range.openClosed(from, to);
		return coordinatesRepository.findByFloorIdAndInRange(filter.getFloorId(), range)
			.stream().map(UwbCoordinatesDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AreaEvent> getAreaEvents(ReportFilterDto filter) {
		logger.debug("Trying to retrive area events in date range {} - {}", filter.getFrom(), filter.getTo());
		Map<Integer, List<Area>> tagInArea = new HashMap<>();
		List<AreaEvent> events = new ArrayList<>();
		List<UwbCoordinatesDto> coordinates = getCoordinates(filter);
		coordinates.forEach(coordinatesDto -> {
			List<Area> areas = areaRepository.findAreasThePointIsWithin(coordinatesDto.getPoint().getX(), coordinatesDto.getPoint().getY());
			Integer tagShortId = coordinatesDto.getTagShortId();
			LocalDateTime date = LocalDateTime.ofInstant(coordinatesDto.getDate().toInstant(), ZoneId.systemDefault());
			if (!tagInArea.containsKey(tagShortId) && !areas.isEmpty()) {
				logger.debug("The tag {} has entered the area", tagShortId);
				areas.forEach(area -> {
					events.add(new AreaEvent(AreaConfiguration.Mode.ON_ENTER, area.getId(), area.getName(), tagShortId, date));
				});
				tagInArea.put(tagShortId, areas);
			} else if (tagInArea.containsKey(tagShortId) && areas.isEmpty()) {
				logger.debug("The tag {} has left the area", tagShortId);
				tagInArea.get(tagShortId).forEach(area -> {
					events.add(new AreaEvent(AreaConfiguration.Mode.ON_LEAVE, area.getId(), area.getName(), tagShortId, date));
				});
				tagInArea.get(tagShortId).clear();
			} else if (tagInArea.containsKey(tagShortId) && !areas.isEmpty()) {
				logger.debug("The tag {} has already been in the area. No area event to be sent");
				tagInArea.put(tagShortId, areas);
			}
		});

		return events;
	}

}
