package co.blastlab.serviceblbnavi.rest.facade.building;

import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/buildings")
@Api("/buildings")
@TokenAuthorization
public interface BuildingFacade {

	@POST
	@ApiOperation(value = "create building", response = BuildingDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id doesn't exist or complex empty")
	})
	BuildingDto create(@ApiParam(value = "building", required = true) @Valid BuildingDto building);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update building by id", response = BuildingDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building doesn't exist or complex doesn't contain given building")
	})
	BuildingDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                                  @ApiParam(value = "building", required = true) @Valid BuildingDto building);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete building by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id doesn't exist"),
		@ApiResponse(code = 204, message = "deleted successfully but there is no new information to return")
	})
	Response delete(@PathParam("id") @ApiParam(value = "building id", required = true) Long id);

	@GET
	@Path("/{id: \\d+}/floors")
	@ApiOperation(value = "find building by id (include floors)", response = BuildingDto.WithFloors.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id wasn't found")
	})
	BuildingDto.WithFloors find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);
}