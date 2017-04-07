package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/floors")
@Api("/floors")
@TokenAuthorization
public interface FloorFacade {

	@POST
	@ApiOperation(value = "create floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Building id does not exist or building id empty"),
		@ApiResponse(code = 400, message = "Level and building id must be unique")
	})
	FloorDto create(@ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Building id does not exist or building id empty")
	})
	FloorDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                       @ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@PUT
	@Path("/")
	@ApiOperation(value = "update floors levels", response = FloorDto.class, responseContainer = "list")
	@ApiResponses({
		@ApiResponse(code = 400, message = "Validation failed"),
		@ApiResponse(code = 404, message = "One of the floors does not exist")
	})
	List<FloorDto> updateLevels(@ApiParam(value = "floors", required = true) @Valid List<FloorDto> floors) throws Exception;

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete floor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	Response delete(@PathParam("id") @ApiParam(value = "floor id", required = true) @Valid @NotNull Long id);
}