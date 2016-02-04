package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Waypoint;
import java.util.List;
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

    public List<Waypoint> findByBuildingId(Long buildingId) {
        return em.createNamedQuery(Waypoint.FIND_BY_BUILDING_ID, Waypoint.class).setParameter("buildingId", buildingId).getResultList();
    }

    public void insertSQL(Waypoint waypoint, EntityManager em) {
        em.createNativeQuery("INSERT INTO Waypoint (id, x, y, timeToCheckout, distance, details, floor_id, inactive, name) VALUES (:id, :x, :y, :timeToCheckout, :distance, :details, :floor_id, :inactive, :name)")
                .setParameter("id", waypoint.getId())
                .setParameter("x", waypoint.getX())
                .setParameter("y", waypoint.getY())
                .setParameter("timeToCheckout", waypoint.getTimeToCheckout())
                .setParameter("distance", waypoint.getDistance())
                .setParameter("details", waypoint.getDetails())
                .setParameter("floor_id", waypoint.getFloor().getId())
                .setParameter("inactive", waypoint.getInactive())
                .setParameter("name", waypoint.getName())
                .executeUpdate();
    }
    
}
