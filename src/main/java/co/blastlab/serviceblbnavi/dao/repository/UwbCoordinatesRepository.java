package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.UwbCoordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UwbCoordinatesRepository extends EntityRepository<UwbCoordinates, Long> {
	@Query(
		value =
			"select c.id, c.creationDate, c.modificationDate, round(avg(c.x)) as x, round(avg(c.y)) as y, c.floor_id, round(avg(c.z)) as z, uc.tag_id " +
			"from coordinates c inner join uwbcoordinates uc on c.id = uc.id " +
			"where c.floor_id = ?1 and c.creationDate >= ?2 and creationDate <= ?3 " +
			"group by c.creationDate " +
			"order by c.creationDate",
		isNative = true
	)
	List<UwbCoordinates> findByFloorIdAndInDateRange(Long floorId, LocalDateTime dateFrom, LocalDateTime dateTo);
}
