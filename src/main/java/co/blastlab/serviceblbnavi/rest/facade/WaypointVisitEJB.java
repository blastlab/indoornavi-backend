package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.WaypointVisitBean;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import co.blastlab.serviceblbnavi.dto.waypoint.WaypointVisitDto;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;


public class WaypointVisitEJB {

    @Inject
    private WaypointVisitBean waypointVisitBean;

    @Inject
    private WaypointBean waypointBean;

    public WaypointVisitDto create(WaypointVisitDto waypointVisit) {
        Waypoint waypoint = waypointBean.findById(waypointVisit.getWaypointId());
        if (waypoint != null) {
            WaypointVisit waypointVisitEntity = new WaypointVisit();
            waypointVisitEntity.setWaypoint(waypoint);
            waypointVisitEntity.setDevice(waypointVisit.getDevice());
            waypointVisitEntity.setCreationDateTimestamp(waypointVisit.getTimestamp());
            waypointVisitBean.create(waypointVisitEntity);
            return new WaypointVisitDto(waypointVisitEntity);
        }
        throw new EntityNotFoundException();
    }

}