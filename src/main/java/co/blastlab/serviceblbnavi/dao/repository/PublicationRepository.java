package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Publication;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationRepository extends EntityRepository<Publication, Long> {
	Optional<Publication> findOptionalById(Long id);
	List<Publication> findByFloor(Floor floor);
}
