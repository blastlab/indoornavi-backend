package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Anchor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface AnchorRepository extends EntityRepository<Anchor, Long> {

	Optional<Anchor> findById(Long id);

	Anchor findByShortId(Integer shortId);

	Optional<Anchor> findOptionalByShortId(Integer shortId);

}