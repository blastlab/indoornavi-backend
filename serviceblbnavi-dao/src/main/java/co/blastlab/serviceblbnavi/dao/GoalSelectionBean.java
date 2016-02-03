package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.GoalSelection;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class GoalSelectionBean {

    @Inject
    private EntityManager em;

    public void create(GoalSelection goalSelection) {
        em.persist(goalSelection);
    }

    public void insetSQL(GoalSelection goalSelection) {
        em.createNativeQuery("INSERT INTO GoalSelection (id, device, x, y, floor_level, creationDateTimestamp, goal_id) VALUES (:id, :device, :x, :y, :floor_level, :creationDateTimestamp, :goal_id)")
                .setParameter("id", goalSelection.getId())
                .setParameter("device", goalSelection.getDevice())
                .setParameter("x", goalSelection.getX())
                .setParameter("y", goalSelection.getY())
                .setParameter("floor_level", goalSelection.getFloorLevel())
                .setParameter("creationDateTimestamp", goalSelection.getCreationDateTimestamp())
                .setParameter("goal_id", goalSelection.getGoal().getId())
                .executeUpdate();
    }

}
