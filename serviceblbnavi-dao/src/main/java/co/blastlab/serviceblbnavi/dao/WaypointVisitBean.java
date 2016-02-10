package co.blastlab.serviceblbnavi.dao;

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
    private EntityManager em;

    public void create(WaypointVisit waypointVisit) {
        em.persist(waypointVisit);
    }
}
