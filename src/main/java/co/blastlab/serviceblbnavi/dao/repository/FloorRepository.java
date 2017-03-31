package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends EntityRepository<Floor, Long> {

	Optional<Floor> findById(Long id);
}
