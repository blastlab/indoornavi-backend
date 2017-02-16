package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.WaypointRepository;
import co.blastlab.serviceblbnavi.dao.repository.WaypointVisitProductionRepository;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

@Stateless
public class WaypointVisitEJB {

    @Inject
    private WaypointVisitProductionRepository waypointVisitProductionRepository;

    @Inject
    private WaypointRepository waypointRepository;

    public WaypointVisit create(WaypointVisit waypointVisit) {
        if (waypointVisit.getWaypointId() != null && waypointVisit.getDevice() != null
                && (waypointVisit.getCreationDateTimestamp() != null || waypointVisit.getTimestamp() != null)) {
            if (waypointVisit.getTimestamp() != null) {
                waypointVisit.setCreationDateTimestamp(waypointVisit.getTimestamp());
                waypointVisit.setTimestamp(null);
            }
            Waypoint waypoint = waypointRepository.findBy(waypointVisit.getWaypointId());
            if (waypoint != null) {
                waypointVisit.setWaypoint(waypoint);
                waypointVisitProductionRepository.save(waypointVisit);
                return waypointVisit;
            }
        }
        throw new EntityNotFoundException();
    }

}