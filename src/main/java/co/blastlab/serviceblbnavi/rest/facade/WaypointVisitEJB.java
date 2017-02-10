package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.WaypointVisitBean;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;


public class WaypointVisitEJB {

    @Inject
    private WaypointVisitBean waypointVisitBean;

    @Inject
    private WaypointBean waypointBean;


    public WaypointVisit create(WaypointVisit waypointVisit) {
        if (waypointVisit.getWaypointId() != null && waypointVisit.getDevice() != null
                && (waypointVisit.getCreationDateTimestamp() != null || waypointVisit.getTimestamp() != null)) {
            if (waypointVisit.getTimestamp() != null) {
                waypointVisit.setCreationDateTimestamp(waypointVisit.getTimestamp());
                waypointVisit.setTimestamp(null);
            }
            Waypoint waypoint = waypointBean.findById(waypointVisit.getWaypointId());
            if (waypoint != null) {
                waypointVisit.setWaypoint(waypoint);
                waypointVisitBean.create(waypointVisit);
                return waypointVisit;
            }
        }
        throw new EntityNotFoundException();
    }

}