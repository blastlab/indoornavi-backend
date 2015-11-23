package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Vertex;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author mkoszalka
 */
@Stateless
public class VertexBean {

    @EJB
    private GoalBean goalBean;

    @Inject
    private EntityManager em;

    public void create(Vertex vertex) {
        em.persist(vertex);
    }

    public Vertex find(Long id) {
        return em.find(Vertex.class, id);
    }

    public List<Vertex> findAll(Long floorId) {
        return em.find(Floor.class, floorId).getVertices();
    }

    public void delete(Vertex vertex) {
        em.remove(em.contains(vertex) ? vertex : em.merge(vertex));
        em.flush();
    }

    public void deactivate(Vertex vertex) {
        List<Goal> goals = vertex.getGoals();
        for (Goal goal : goals) {
            goal.setInactive(true);
        }
        goalBean.update(goals);
        vertex.setInactive(true);
        update(vertex);
    }

    public void update(Vertex vertex) {
        em.merge(vertex);
    }

    public void update(List<Vertex> vertexes) {
        for (Vertex v : vertexes) {
            update(v);
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

}
