package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Tag;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends EntityRepository<Tag, Long> {

	Optional<Tag> findOptionalById(Long id);
	Optional<Tag> findOptionalByShortId(Integer shortId);
}
