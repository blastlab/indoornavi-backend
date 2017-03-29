package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Anchor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface AnchorRepository extends EntityRepository<Anchor, Long> {
	List<Anchor> findByVerified(Boolean isVerified);
}
