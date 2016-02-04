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

    public void insertSQL(WaypointVisit waypointVisit, EntityManager em) {
        em.createNativeQuery("INSERT INTO WaypointVisit (id, device, creationDateTimestamp, waypoin_id) VALUES (:id, :device, :creationDateTimestamp, :waypoint_id)")
                .setParameter("id", waypointVisit.getId())
                .setParameter("device", waypointVisit.getDevice())
                .setParameter("creationDateTimestamp", waypointVisit.getCreationDateTimestamp())
                .setParameter("waypoint_id", waypointVisit.getWaypoint().getId())
                .executeUpdate();
    }
    
}
