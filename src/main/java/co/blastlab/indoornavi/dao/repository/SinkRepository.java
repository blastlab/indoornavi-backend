package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SinkRepository extends EntityRepository<Sink, Long> {
	Optional<Sink> findOptionalByShortId(Integer shortId);
	Optional<Sink> findOptionalById(Long id);
	List<Sink> findByConfigured(boolean configured);
	List<Sink> findByFloor(Floor floor);
	@Query(named = Sink.ALL_WITH_FLOOR)
	List<Sink> findAllWithFloor();
}
