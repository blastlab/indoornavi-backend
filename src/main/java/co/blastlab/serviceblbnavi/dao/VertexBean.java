package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Vertex;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author mkoszalka
 */
@Stateless
public class VertexBean {

    @Inject
    private EntityManager em;

    @Inject
    VertexRepository vertexRepository;

    public Vertex find(Long id) {
        return em.find(Vertex.class, id);
    }

    public void deactivate(Vertex vertex) {
        vertex.setInactive(true);
        vertexRepository.save(vertex);
    }
}
