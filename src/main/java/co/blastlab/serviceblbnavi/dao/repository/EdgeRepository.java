package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface EdgeRepository extends EntityRepository<Edge, Long> {

    Edge findOptionalBySourceAndTarget(Vertex source, Vertex target);

    List<Edge> findBySource(Vertex source);
}
