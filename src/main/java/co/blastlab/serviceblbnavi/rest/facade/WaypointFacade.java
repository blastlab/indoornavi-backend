package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import java.util.List;

@Path("/waypoint")
@Api("/waypoint")
public interface WaypointFacade {

    @POST
    @ApiOperation(value = "create waypoint", response = Waypoint.class)
    @ApiResponses({
            @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    Waypoint createWaypoint(@ApiParam(value = "waypoint", required = true) Waypoint waypoint);

    @PUT
    @ApiOperation(value = "update waypoint's data", response = Waypoint.class)
    @ApiResponses({
            @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    Waypoint updateWaypoint(@ApiParam(value = "waypoint", required = true) Waypoint waypoint);

    @PUT
    @Path("/coordinates")
    @ApiOperation(value = "update waypoint's coordinates", response = Waypoint.class)
    @ApiResponses({
            @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    Waypoint updateWaypointsCoordinates(@ApiParam(value = "waypoint", required = true) Waypoint waypoint);

    @GET
    @Path("/floor/{id: \\d+}/active")
    @ApiOperation(value = "gets active waypoints by floor id", response = Waypoint.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id was not found")
    })
    List<Waypoint> getActiveWaypointsByFloorId(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);

    @GET
    @Path("/building/{id: \\d+}")
    @ApiOperation(value = "gets waypoints by building id", response = Waypoint.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id was not found")
    })
    List<Waypoint> getWaypointsByBuildingId(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId);

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.WaypointInternal.class)
    @ApiOperation(value = "deactivates waypoint of given id", response = Waypoint.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "waypoint with given id wasn't found")
    })
    Waypoint deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long waypointId);
}
