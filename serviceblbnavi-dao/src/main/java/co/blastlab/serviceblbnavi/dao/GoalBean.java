package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Goal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class GoalBean {

    @Inject
    private EntityManager em;

    public void create(Goal goal) {
        em.persist(goal);
    }

    public Goal find(Long id) {
        return em.find(Goal.class, id);
    }

    public void delete(Goal goal) {
        em.remove(em.contains(goal) ? goal : em.merge(goal));
    }

    public void update(Goal goal) {
        em.merge(goal);
    }

    public void update(List<Goal> goals) {
        goals.stream().forEach((v) -> {
            update(v);
        });
    }

    public void deactivate(Goal goal) {
        goal.setInactive(true);
        update(goal);
    }

    public List<Goal> findAllByBuildingId(Long buildingId) {
        return em.createNamedQuery(Goal.FIND_BY_BUILDING, Goal.class)
                .setParameter("buildingId", buildingId).getResultList();
    }

    public List<Goal> findAllByFloorId(Long floorId) {
        return em.createNamedQuery(Goal.FIND_BY_FLOOR, Goal.class)
                .setParameter("floorId", floorId).getResultList();
    }

    public void insertSQL(Goal goal, EntityManager em) {
        em.createNativeQuery("INSERT INTO Goal (id, x, y, name, floor_id, inactive) VALUES (:id, :x, :y, :name, :floor_id, :inactive)")
                .setParameter("id", goal.getId())
                .setParameter("x", goal.getX())
                .setParameter("y", goal.getY())
                .setParameter("name", goal.getName())
                .setParameter("floor_id", goal.getFloor().getId())
                .setParameter("inactive", goal.getInactive())
                .executeUpdate();
    }

    public List<Goal> findActiveByFloorId(Long floorId) {
        return em.createNamedQuery(Goal.FIND_ACTIVE_BY_FLOOR, Goal.class)
                .setParameter("floorId", floorId).getResultList();
    }
}
