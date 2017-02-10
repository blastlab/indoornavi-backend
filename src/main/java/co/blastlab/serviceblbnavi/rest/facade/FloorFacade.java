package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/floor")
@Api("/floor")
public interface FloorFacade {

    @POST
    @ApiOperation(value = "create floor", response = Floor.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    Floor create(@ApiParam(value = "floor", required = true) Floor floor);

    @PUT
    @JsonView(View.FloorInternal.class)
    @ApiOperation(value = "update floor", response = Floor.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    Floor update(@ApiParam(value = "floor", required = true) Floor floor);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete floor", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.FloorInternal.class)
    @ApiOperation(value = "find floor", response = Floor.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    Floor find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @PUT
    @Path("/{id: \\d+}")
    @ApiOperation(value = "update floors", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    Response updateFloors(@PathParam("id") Long buildingId, @ApiParam(value = "floors", required = true) List<Floor> floors);

    @PUT
    @Path("/mToPix")
    @ApiOperation(value = "update mToPix", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    Response updatemToPix(@ApiParam(value = "floor", required = true) Floor floor);
}
