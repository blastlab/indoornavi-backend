package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Vertex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public abstract class VertexRepository implements EntityRepository<Vertex, Long> {

    public abstract List<Vertex> findByFloorAndInactive(Floor floor, Boolean inactive);

    public abstract List<Vertex> findByFloor(Floor floor);

    public void save(List<Vertex> vertices) {
        vertices.forEach(this::save);
    }
}
