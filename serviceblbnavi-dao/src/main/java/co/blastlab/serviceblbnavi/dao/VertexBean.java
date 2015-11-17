package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import java.util.ArrayList;
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
        return em.find(Vertex.class, id);
    }

    public List<Vertex> findAll(Long floorId) {
        return em.createNamedQuery(Vertex.FIND_BY_FLOOR, Vertex.class).setParameter("floorId", floorId).getResultList();
    }

    public void deleteWithEdgesCheck(Vertex vertex) {
        System.out.println("deleteWithEdgesCheck");
        List<Vertex> verticesToCheckFloorChangeability = new ArrayList<>();
        for (Edge edge : vertex.getTargetEdges()) {
            verticesToCheckFloorChangeability.add(edge.getSource());
            System.out.println("added id: " + edge.getSource().getId());
        }
        delete(vertex);
        for (Vertex vertexToCheckFloorChangeability : verticesToCheckFloorChangeability) {
            System.out.println("updating floor changeability of: " + vertexToCheckFloorChangeability.getId());
            updateFloorChangeability(vertexToCheckFloorChangeability);
        }
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
    
    public void updateFloorChangeability(Vertex vertex) {
        vertex.setIsFloorDownChangeable(false);
        vertex.setIsFloorUpChangeable(false);
        for (Edge edge : vertex.getSourceEdges()) {
            if (edge.getTarget().getFloor().getLevel() < vertex.getFloor().getLevel()) {
                vertex.setIsFloorDownChangeable(true);
            } else if (edge.getTarget().getFloor().getLevel() > vertex.getFloor().getLevel()) {
                vertex.setIsFloorUpChangeable(true);
            }
        }
        update(vertex);
    }
}
