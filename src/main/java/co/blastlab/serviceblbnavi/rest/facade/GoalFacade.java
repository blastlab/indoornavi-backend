package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.dto.goal.GoalDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/goal")
@Api("/goal")
@TokenAuthorization
public interface GoalFacade {

    @POST
    @ApiOperation(value = "create goal", response = GoalDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id emtpy or floor doesn't exist")
    })
    GoalDto create(@ApiParam(value = "goal", required = true) @Valid GoalDto goal);

    @PUT
    @Path("/name")
    @ApiOperation(value = "update goal name", response = GoalDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    GoalDto updateName(@ApiParam(value = "goal", required = true) @Valid GoalDto goal);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete goal", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @PUT
    @Path("/coordinates")
    @ApiOperation(value = "update goal coordinates", response = GoalDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    GoalDto updateCoordinates(@ApiParam(value = "goal", required = true) @Valid GoalDto goal);

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @ApiOperation(value = "deactivates goal of given id", response = GoalDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    GoalDto deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long goalId);

    @GET
    @Path("/building/{id: \\d+}")
    @ApiOperation(value = "find goals for specified building", response = GoalDto.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building with given id wasn't found")
    })
    List<GoalDto> findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId);

    @GET
    @Path("/floor/{id: \\d+}")
    @ApiOperation(value = "find goals for specified floor", response = GoalDto.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<GoalDto> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);

    @GET
    @Path("/floor/{id: \\d+}/active")
    @ApiOperation(value = "find goals for specified floor", response = GoalDto.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<GoalDto> findActiveByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);
}
