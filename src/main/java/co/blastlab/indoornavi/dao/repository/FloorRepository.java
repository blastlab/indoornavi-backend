package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends EntityRepository<Floor, Long> {

	Optional<Floor> findOptionalById(Long id);
}
