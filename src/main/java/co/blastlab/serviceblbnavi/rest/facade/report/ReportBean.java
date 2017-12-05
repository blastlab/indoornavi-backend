package co.blastlab.serviceblbnavi.rest.facade.report;

import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.report.ReportFilterDto;
import com.google.common.collect.Range;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportBean implements ReportFacade {

	@Inject
	private CoordinatesRepository coordinatesRepository;

	@Override
	public List<CoordinatesDto> getCoordinates(ReportFilterDto filter) {
		LocalDateTime from = filter.getFrom() != null ? filter.getFrom() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusYears(50);
		LocalDateTime to = filter.getTo() != null ? filter.getTo() : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
		Range<LocalDateTime> range = Range.open(from, to);
		return coordinatesRepository.findByFloorIdAndInRange(filter.getFloorId(), range)
			.stream().map(CoordinatesDto::new).collect(Collectors.toList());
	}
}
