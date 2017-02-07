package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.WaypointVisitBean;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/waypointVisit")
@Api("/waypointVisit")
@Produces("application/json")
public class WaypointVisitFacade {

    @Inject
    private WaypointVisitBean waypointVisitBean;

    @Inject
    private WaypointBean waypointBean;

    @POST
    @ApiOperation(value = "create waypoint visit", response = WaypointVisit.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "invalid waypoint visit\'s data")
    })
    @JsonView(View.WaypointVisitInternal.class)
    public WaypointVisit create(@ApiParam(value = "waypoint visit", required = true) WaypointVisit waypointVisit) {
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
