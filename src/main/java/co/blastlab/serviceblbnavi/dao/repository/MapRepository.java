package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Map;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface MapRepository extends EntityRepository<Map, Long> {
	Optional<Map> findOptionalById(Long id);
}
