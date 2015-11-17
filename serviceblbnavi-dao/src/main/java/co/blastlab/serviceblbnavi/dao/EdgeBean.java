package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 *
 * @author mkoszalka
 */
@Stateless
public class EdgeBean {

    @Inject
    private EntityManager em;

    public void create(Edge edge) {
        em.persist(edge);
    }

    public Edge find(Long id) {
        return em.find(Edge.class, id);
    }

    public Edge findBySourceAndTarget(Vertex source, Vertex target) {
        Edge edge;
        try {
            edge = em.createNamedQuery(Edge.FIND_BY_TARGET_AND_SOURCE, Edge.class).setParameter("source", source).setParameter("target", target).getSingleResult();
        } catch (NoResultException e) {
            edge = null;
        }
        return edge;
    }

    public List<Edge> findByVertexFloorId(Long id) {
        return em.createNamedQuery(Edge.FIND_VERTEX_FLOOR_ID, Edge.class).setParameter("floorId", id).getResultList();
    }

    public void delete(Edge edge) {
        em.remove(em.contains(edge) ? edge : em.merge(edge));
    }

    public void update(Edge edge) {
        em.merge(edge);
    }

    public void update(List<Edge> edges) {
        for (Edge e : edges) {
            update(e);
        }
    }
    
    public void clearEdges(Vertex vertex) {
    }

}
