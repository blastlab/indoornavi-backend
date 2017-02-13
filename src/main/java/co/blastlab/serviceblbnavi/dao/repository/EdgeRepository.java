package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface EdgeRepository extends EntityRepository<Edge, Long> {

    public Edge findOptionalBySourceAndTarget(Vertex source, Vertex target);
}
