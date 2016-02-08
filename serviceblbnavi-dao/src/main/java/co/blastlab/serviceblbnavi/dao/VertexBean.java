package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Floor;
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
        return em.find(Vertex.class, id);
    }

    public List<Vertex> findAll(Long floorId) {
        return em.find(Floor.class, floorId).getVertices();
    }

    public List<Vertex> findAllActive(Long floorId) {
        List<Vertex> vertices = em.createNamedQuery(Vertex.FIND_ACTIVE_BY_FLOOR, Vertex.class).setParameter("floorId", floorId).getResultList();
        return vertices;
    }

    public void delete(Vertex vertex) {
        em.remove(em.contains(vertex) ? vertex : em.merge(vertex));
        em.flush();
    }

    public void deactivate(Vertex vertex) {
        vertex.setInactive(true);
        update(vertex);
    }

    public void update(Vertex vertex) {
        em.merge(vertex);
    }

    public void update(List<Vertex> vertexes) {
        vertexes.stream().forEach((v) -> {
            update(v);
        });
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void insertSQL(Vertex vertex, EntityManager em) {
        em.createNativeQuery("INSERT INTO Vertex (id, x, y, floor_id, inactive) VALUES (:id, :x, :y, :floor_id, :inactive)")
                .setParameter("id", vertex.getId())
                .setParameter("x", vertex.getX())
                .setParameter("y", vertex.getY())
                .setParameter("floor_id", vertex.getFloor().getId())
                .setParameter("inactive", vertex.getInactive())
                .executeUpdate();
    }

}
