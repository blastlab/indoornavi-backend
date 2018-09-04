package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.*;
import com.google.common.collect.Range;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.Criteria;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
public abstract class UwbCoordinatesRepository implements EntityRepository<UwbCoordinates, Long>, CriteriaSupport<UwbCoordinates> {
	public abstract UwbCoordinates findByDevice(String device);

	public List<UwbCoordinates> findByFloorIdAndInRange(Long floorId, Range<LocalDateTime> range) {
		Date from = Date.from(range.lowerEndpoint().atZone(ZoneId.systemDefault()).toInstant());
		Date to = Date.from(range.upperEndpoint().atZone(ZoneId.systemDefault()).toInstant());
		Criteria<UwbCoordinates, UwbCoordinates> criteria = criteria()
			.between(Coordinates_.creationDate, from, to);
		if (floorId != null) {
			criteria = criteria.join(UwbCoordinates_.floor,  where(Floor.class).eq(Floor_.id, floorId));
		}
		return criteria.orderAsc(UwbCoordinates_.creationDate).getResultList();
	}
}
