package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/goal")
@Api("/goal")
@TokenAuthorization
public interface GoalFacade {

    @POST
    @ApiOperation(value = "create goal", response = Goal.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id emtpy or floor doesn't exist")
    })
    @JsonView(View.GoalInternal.class)
    Goal create(@ApiParam(value = "goal", required = true) Goal goal);

    @PUT
    @Path("/name")
    @ApiOperation(value = "update goal name", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    Goal updateName(@ApiParam(value = "goal", required = true) Goal goal);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete goal", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @PUT
    @Path("/coordinates")
    @ApiOperation(value = "update goal coordinates", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    Goal updateCoordinates(@ApiParam(value = "goal", required = true) Goal goal);

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "deactivates goal of given id", response = Goal.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    Goal deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long goalId);

    @GET
    @Path("/building/{id: \\d+}")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified building", response = Goal.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building with given id wasn't found")
    })
    List<Goal> findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId);

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified floor", response = Goal.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<Goal> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);

    @GET
    @Path("/floor/{id: \\d+}/active")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified floor", response = Goal.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<Goal> findActiveByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);
}
