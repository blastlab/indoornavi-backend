package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.dto.beacon.BeaconDto;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/beacon")
@Api("/beacon")
public interface BeaconFacade {

    @POST
    @ApiOperation(value = "create beacon", response = BeaconDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    BeaconDto create(@ApiParam(value = "beacon", required = true) BeaconDto beacon);

    @PUT
    @ApiOperation(value = "update beacon", response = BeaconDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id or floor empty or doesn't exist")
    })
    BeaconDto update(@ApiParam(value = "beacon", required = true) BeaconDto beacon);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete beacon", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "beacon with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find beacon", response = BeaconDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "beacon with given id wasn't found")
    })
    BeaconDto find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/floor/{id: \\d+}")
    @ApiOperation(value = "find beacons by floor id", response = BeaconDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floorId empty or floor doesn't exist")
    })
    List<BeaconDto> findAll(@PathParam("id") @ApiParam(value = "id", required = true) Long floorId);
}