package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Vertex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface VertexRepository extends EntityRepository<Vertex, Long> {

    public Vertex findOptionalBy(Long id);
}
