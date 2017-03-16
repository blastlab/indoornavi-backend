package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/buildings")
@Api("/buildings")
@TokenAuthorization
public interface BuildingFacade {

	@POST
	@ApiOperation(value = "create building", response = BuildingDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id doesn't exist or complex empty")
	})
	BuildingDto.WithId create(@ApiParam(value = "building", required = true) @Valid BuildingDto building);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update building by id", response = BuildingDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building doesn't exist or complex doesn't contain given building")
	})
	BuildingDto.WithId update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull() Long id,
	                                  @ApiParam(value = "building", required = true) @Valid BuildingDto building);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete building by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id doesn't exist")
	})
	Response delete(@PathParam("id") @ApiParam(value = "building id", required = true) Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find building by id", response = BuildingDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id wasn't found")
	})
	BuildingDto.WithId find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);
}