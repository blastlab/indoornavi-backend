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

    public List<Goal> findAll(Long vertexId) {
        return em.createNamedQuery(Goal.FIND_BY_VERTEX, Goal.class)
                .setParameter("vertexId", vertexId).getResultList();
    }

    public void delete(Goal goal) {
        em.remove(em.contains(goal) ? goal : em.merge(goal));
    }

    public void update(Goal goal) {
        em.merge(goal);
    }

    public void update(List<Goal> goals) {
        for (Goal v : goals) {
            update(v);
        }
    }

    public void deactivate(Goal goal) {
        goal.setInactive(true);
        update(goal);
    }
}
