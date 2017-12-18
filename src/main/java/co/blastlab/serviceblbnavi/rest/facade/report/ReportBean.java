package co.blastlab.serviceblbnavi.rest.facade.report;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.report.ReportFilterDto;
import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import com.google.common.collect.Range;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class ReportBean implements ReportFacade {

	@Inject
	private CoordinatesRepository coordinatesRepository;

	@Inject
	private AreaRepository areaRepository;

	@Override
	public List<CoordinatesDto> getCoordinates(ReportFilterDto filter) {
		LocalDateTime from = filter.getFrom() != null ? filter.getFrom() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusYears(50);
		LocalDateTime to = filter.getTo() != null ? filter.getTo() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
		Range<LocalDateTime> range = Range.open(from, to);
		return coordinatesRepository.findByFloorIdAndInRange(filter.getFloorId(), range)
			.stream().map(CoordinatesDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AreaEvent> getAreaEvents(ReportFilterDto filter) {
		Map<Integer, List<Area>> tagInArea = new HashMap<>();
		List<AreaEvent> events = new ArrayList<>();
		List<CoordinatesDto> coordinates = getCoordinates(filter);
		coordinates.forEach(coordinatesDto -> {
			List<Area> areas = areaRepository.findAreasThePointIsWithin(coordinatesDto.getPoint().getX(), coordinatesDto.getPoint().getY());
			Integer tagShortId = coordinatesDto.getTagShortId();
			LocalDateTime date = LocalDateTime.ofInstant(coordinatesDto.getDate().toInstant(), ZoneId.systemDefault());
			if (!tagInArea.containsKey(tagShortId) && !areas.isEmpty()) {
				// the tag has entered the areas
				areas.forEach(area -> {
					events.add(new AreaEvent(AreaConfiguration.Mode.ON_ENTER, area.getId(), area.getName(), tagShortId, date));
				});
				tagInArea.put(tagShortId, areas);
			} else if (tagInArea.containsKey(tagShortId) && areas.isEmpty()) {
				// the tag has left the areas
				tagInArea.get(tagShortId).forEach(area -> {
					events.add(new AreaEvent(AreaConfiguration.Mode.ON_LEAVE, area.getId(), area.getName(), tagShortId, date));
				});
				tagInArea.get(tagShortId).clear();
			} else if (tagInArea.containsKey(tagShortId) && !areas.isEmpty()) {
				// the tag has already been in the areas
				tagInArea.put(tagShortId, areas);
			}
		});

		return events;
	}

}
