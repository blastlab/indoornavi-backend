package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Vertex;
import java.util.List;
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

    public void create(Vertex vertex) {
        em.persist(vertex);
    }

    public Vertex find(Long id) {
         return em.createNamedQuery(Vertex.FIND_WITH_FLOOR_CHANGEABILITY, Vertex.class).setParameter("id", id).getSingleResult();
    }

    public List<Vertex> findAll(Long floorId) {
        return em.createNamedQuery(Vertex.FIND_BY_FLOOR_WITH_FLOOR_CHANGEABILITY, Vertex.class).setParameter("floorId", floorId).getResultList();
    }
    
    public void delete(Vertex vertex) {
        em.remove(em.contains(vertex) ? vertex : em.merge(vertex));
        em.flush();
    }

    public void update(Vertex vertex) {
        em.merge(vertex);
    }

    public void update(List<Vertex> vertexes) {
        for (Vertex v : vertexes) {
            update(v);
        }
    }
}
