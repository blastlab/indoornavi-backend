package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.UwbCoordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UwbCoordinatesRepository extends EntityRepository<UwbCoordinates, Long> {

	String REQUIRED_FIELDS = "c.id, c.creationDate, c.modificationDate, round(avg(c.x)) as x, round(avg(c.y)) as y, c.floor_id, c.measurementTime, round(avg(c.z)) as z, c.tag_id";
	String GROUP_BY_FIELDS = "c.creationDate, c.floor_id, c.tag_id";

	@Query(
		value =
			"select " + REQUIRED_FIELDS + " " +
				"from uwbcoordinates c " +
				"where c.floor_id = ?1 and c.creationDate >= ?2 and c.creationDate <= ?3 and c.tag_id in (?4) " +
				"group by " + GROUP_BY_FIELDS + " " +
				"order by c.creationDate",
		isNative = true
	)
	List<UwbCoordinates> findByFloorAndTagsAndInDateRange(Long floorId, LocalDateTime dateFrom, LocalDateTime dateTo, List<Long> tagsIds);

	@Query(
		value =
			"select " + REQUIRED_FIELDS + " " +
				"from uwbcoordinates c " +
				"where c.floor_id = ?1 and c.creationDate >= ?2 and c.creationDate <= ?3 " +
				"group by " + GROUP_BY_FIELDS + " " +
				"order by c.creationDate",
		isNative = true
	)
	List<UwbCoordinates> findByFloorIdAndInDateRange(Long floorId, LocalDateTime dateFrom, LocalDateTime dateTo);

	@Query(
		value =
			"select " + REQUIRED_FIELDS + " " +
				"from uwbcoordinates c  " +
				"where c.creationDate >= ?1 and c.creationDate <= ?2 " +
				"group by " + GROUP_BY_FIELDS + " " +
				"order by c.creationDate",
		isNative = true
	)
	List<UwbCoordinates> findByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo);

	@Query(
		value =
			"select " + REQUIRED_FIELDS + " " +
				"from uwbcoordinates c " +
				"where c.creationDate >= ?1 and c.creationDate <= ?2 and c.tag_id in ?3 " +
				"group by " + GROUP_BY_FIELDS + " " +
				"order by c.creationDate",
		isNative = true
	)
	List<UwbCoordinates> findByTagsAndInDateRange(LocalDateTime from, LocalDateTime to, List<Long> tagsIds);
}
