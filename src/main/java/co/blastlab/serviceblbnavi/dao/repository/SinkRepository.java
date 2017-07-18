package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Sink;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface SinkRepository extends EntityRepository<Sink, Long> {
	Optional<Sink> findOptionalByShortId(Integer shortId);
}