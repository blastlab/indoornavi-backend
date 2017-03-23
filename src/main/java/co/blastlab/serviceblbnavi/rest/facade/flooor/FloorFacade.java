package co.blastlab.serviceblbnavi.rest.facade.flooor;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/floors")
@Api("/floors")
@TokenAuthorization
public interface FloorFacade {

	@POST
	@ApiOperation(value = "create floor", response = FloorDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id doesn't exist or building id empty"),
		@ApiResponse(code = 400, message = "level and building id must be unique")
	})
	FloorDto.WithId create(@ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update floor", response = FloorDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id doesn't exist or building id empty")
	})
	FloorDto.WithId update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                       @ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete floor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id doesn't exist")
	})
	Response delete(@PathParam("id") @ApiParam(value = "floor id", required = true) @Valid @NotNull Long id);
}