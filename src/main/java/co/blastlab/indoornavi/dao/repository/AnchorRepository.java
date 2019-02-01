package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnchorRepository extends EntityRepository<Anchor, Long> {

	Optional<Anchor> findById(Long id);

	Optional<Anchor> findByShortId(Integer shortId);

	Optional<Anchor> findOptionalByShortId(Integer shortId);

	@Query(named = Anchor.BY_SHORT_ID_AND_POSITION_NOT_NULL)
	Optional<Anchor> findOptionalByShortIdAndPositionNotNull(Integer shortId);

	List<Anchor> findByFloor(Floor floor);

}
