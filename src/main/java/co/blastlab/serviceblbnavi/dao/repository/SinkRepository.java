package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Sink;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SinkRepository extends EntityRepository<Sink, Long> {
	Optional<Sink> findOptionalByShortId(Integer shortId);
	List<Sink> findByConfigured(boolean configured);
	List<Sink> findByFloor(Floor floor);
}
