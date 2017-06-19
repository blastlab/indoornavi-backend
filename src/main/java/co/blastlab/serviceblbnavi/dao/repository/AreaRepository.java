package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Area;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends EntityRepository<Area, Long> {
	Optional<Area> findOptionalById(Long id);
}
