package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/floors")
@Api("/floors")
public interface FloorFacade {

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "get specific floor", response = FloorDto.class)
	@AuthorizedAccess("FLOOR_READ")
	FloorDto get(@PathParam("id") @Valid @NotNull Long id);

	@POST
	@ApiOperation(value = "create floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Building id does not exist or building id empty"),
		@ApiResponse(code = 400, message = "Level and building id must be unique")
	})
	@AuthorizedAccess("FLOOR_CREATE")
	FloorDto create(@ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor id does not exist or building id empty")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	FloorDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                       @ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete floor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("FLOOR_DELETE")
	Response delete(@PathParam("id") @ApiParam(value = "floor id", required = true) @Valid @NotNull Long id);

	@PUT
	@Path("/{id: \\d+}/scale")
	@ApiOperation(value = "set scale", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	FloorDto setScale(@PathParam("id") @ApiParam(value = "floor id", required = true) @Valid @NotNull Long id,
	                  @ApiParam(value = "scale", required = true) @Valid ScaleDto scaleDto);
}