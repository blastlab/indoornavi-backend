package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
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
    @NaviProduction
    private EntityManager emProduction;

    public void create(GoalSelection goalSelection) {
        emProduction.persist(goalSelection);
    }
}
