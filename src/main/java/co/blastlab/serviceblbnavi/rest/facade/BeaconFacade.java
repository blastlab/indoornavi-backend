package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/beacon")
@Api("/beacon")
@TokenAuthorization
public interface BeaconFacade {

    @POST
    @ApiOperation(value = "create beacon", response = Beacon.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    Beacon create(@ApiParam(value = "beacon", required = true) Beacon beacon);

    @PUT
    @ApiOperation(value = "update beacon", response = Beacon.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id or floor empty or doesn't exist")
    })
    Beacon update(@ApiParam(value = "beacon", required = true) Beacon beacon);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete beacon", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "beacon with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find beacon", response = Beacon.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "beacon with given id wasn't found")
    })
    Beacon find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/floor/{id: \\d+}")
    @ApiOperation(value = "find beacons by floor id", response = Beacon.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floorId empty or floor doesn't exist")
    })
    List<Beacon> findAll(@PathParam("id") @ApiParam(value = "id", required = true) Long floorId);
}