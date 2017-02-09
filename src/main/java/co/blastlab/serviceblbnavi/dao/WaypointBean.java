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

    public void create(Waypoint waypoint) {
        em.persist(waypoint);
    }

    public List<Waypoint> findActiveByFloorId(Long floorId) {
        return em.createNamedQuery(Waypoint.FIND_ACTIVE_BY_FLOOR_ID, Waypoint.class).setParameter("floorId", floorId).getResultList();
    }

    public void update(Waypoint waypoint) {
        em.merge(waypoint);
    }

    public void deactivate(Waypoint waypoint) {
        waypoint.setInactive(true);
        update(waypoint);
    }
}
