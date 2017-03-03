package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.beacon.BeaconDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/beacon")
@Api("/beacon")
@TokenAuthorization
public interface BeaconFacade {

	@POST
	@ApiOperation(value = "create beacon", response = BeaconDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "beacon id, beacon or floor empty or doesn't exist"),
		@ApiResponse(code = 400, message = "invalid beacon's data (eg duplicate entry 'minor-major')")
	})
	BeaconDto create(@ApiParam(value = "beacon", required = true) @Valid BeaconDto beacon);

	@PUT
	@ApiOperation(value = "update beacon", response = BeaconDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "beacon id, beacon or floor empty or doesn't exist"),
		@ApiResponse(code = 400, message = "invalid beacon's data (duplicate entry 'minor-major')")
	})
	BeaconDto update(@ApiParam(value = "beacon", required = true) @Valid BeaconDto beacon);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete beacon", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "beacon with given id doesn't exist")
	})
	Response delete(@PathParam("id") @ApiParam(value = "beacon id", required = true) Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find beacon", response = BeaconDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "beacon with given id wasn't found")
	})
	BeaconDto find(@PathParam("id") @ApiParam(value = "beacon id", required = true) Long id);

	@GET
	@Path("/floor/{id: \\d+}")
	@ApiOperation(value = "find beacons by floor id", response = BeaconDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floorId empty or floor doesn't exist")
	})
	List<BeaconDto> findAll(@PathParam("id") @ApiParam(value = "floor id", required = true) Long floorId);
}