package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/floor")
@Api("/floor")
public interface FloorFacade {

    @POST
    @ApiOperation(value = "create floor", response = FloorDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    FloorDto create(@ApiParam(value = "floor", required = true) FloorDto floor);

    @PUT
    @ApiOperation(value = "update floor", response = FloorDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    FloorDto update(@ApiParam(value = "floor", required = true) FloorDto floor);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete floor", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find floor", response = FloorDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    FloorDto find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @PUT
    @Path("/{id: \\d+}")
    @ApiOperation(value = "update floors", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    Response updateFloors(@PathParam("id") Long buildingId, @ApiParam(value = "floors", required = true) List<FloorDto> floors);

    @PUT
    @Path("/mToPix")
    @ApiOperation(value = "update mToPix", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    Response updatemToPix(@ApiParam(value = "floor", required = true) FloorDto floor);
}
