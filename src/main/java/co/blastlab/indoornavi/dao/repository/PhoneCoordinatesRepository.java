package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.PhoneCoordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhoneCoordinatesRepository extends EntityRepository<PhoneCoordinates, Long> {
	String REQUIRED_FIELDS = "c.id, c.creationDate, c.modificationDate, round(avg(c.x)) as x, round(avg(c.y)) as y, c.floor_id, c.measurementTime, round(avg(c.z)) as z, pc.phone_id";

	@Query(
		value =
			"select " + REQUIRED_FIELDS + " " +
				"from coordinates c inner join phonecoordinates pc on c.id = pc.id " +
				"where c.floor_id = ?1 and c.creationDate >= ?2 and c.creationDate <= ?3 " +
				"group by c.creationDate " +
				"order by c.creationDate",
		isNative = true
	)
	List<PhoneCoordinates> findByFloorIdAndInDateRange(Long floorId, LocalDateTime dateFrom, LocalDateTime dateTo);
}
