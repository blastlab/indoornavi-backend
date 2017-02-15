package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.waypoint.WaypointVisitDto;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/waypointVisit")
@Api("/waypointVisit")
@Produces("application/json")
public interface WaypointVisitFacade {

    @POST
    @ApiOperation(value = "create waypoint visit", response = WaypointVisitDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid waypoint visit\'s data")
    })
    WaypointVisitDto create(@ApiParam(value = "waypoint visit", required = true) WaypointVisitDto waypointVisit);


}
