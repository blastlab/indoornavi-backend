package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/waypointVisit")
@Api("/waypointVisit")
@Produces("application/json")
public interface WaypointVisitFacade {

    @POST
    @ApiOperation(value = "create waypoint visit", response = WaypointVisit.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid waypoint visit\'s data")
    })
    @JsonView(View.WaypointVisitInternal.class)
    WaypointVisit create(@ApiParam(value = "waypoint visit", required = true) WaypointVisit waypointVisit);


}
