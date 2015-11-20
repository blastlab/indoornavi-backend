package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Waypoint;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class WaypointBean {
    
    @Inject
    private EntityManager em;
    
    public Waypoint findById(Long waypointId) {
        return em.find(Waypoint.class, waypointId);
    }
    
}
