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
}
