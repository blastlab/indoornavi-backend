package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.repository.EdgeRepository;
import co.blastlab.serviceblbnavi.domain.Edge;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 *
 * @author mkoszalka
 */
@Stateless
public class EdgeBean {

    @Inject
    private EntityManager em;

    @Inject
    EdgeRepository edgeRepository;

    public void create(Edge edge) {
        em.persist(edge);
    }

    public Edge findBySourceAndTarget(Long sourceId, Long targetId) {
        Edge edge;
        try {
            edge = em.createNamedQuery(Edge.FIND_BY_TARGET_AND_SOURCE, Edge.class)
                    .setParameter("sourceId", sourceId)
                    .setParameter("targetId", targetId)
                    .getSingleResult();
        } catch (NoResultException e) {
            edge = null;
        }
        return edge;
    }

    public List<Edge> findByVertexFloorId(Long id) {
        return em.createNamedQuery(Edge.FIND_VERTEX_FLOOR_ID, Edge.class).setParameter("floorId", id).getResultList();
    }

    public List<Edge> findByVertexId(Long vertexId) {
        return em.createNamedQuery(Edge.FIND_VERTEX_ID, Edge.class).setParameter("vertexId", vertexId).getResultList();
    }
}
