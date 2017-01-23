package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class WaypointVisitBean {

    @Inject
    @NaviProduction
    private EntityManager emProduction;

    public void create(WaypointVisit waypointVisit) {
        emProduction.persist(waypointVisit);
    }
}
