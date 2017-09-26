package co.blastlab.serviceblbnavi.rest.facade.map;

import co.blastlab.serviceblbnavi.dto.map.MapDto;
import co.blastlab.serviceblbnavi.dto.map.OriginChecker;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/maps")
@Api("/maps")
public interface MapFacade {

	@POST
	@ApiOperation(value = "create new map", response = MapDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("MAP_CREATE")
	MapDto create(@ApiParam(value = "map", required = true) @Valid MapDto map);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "edit map", response = MapDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("MAP_UPDATE")
	MapDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	              @ApiParam(value = "map", required = true) @Valid MapDto map);

	@GET
	@ApiOperation(value = "get all maps", response = MapDto.class, responseContainer = "list")
	@AuthorizedAccess("MAP_READ")
	List<MapDto> getAll();

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "get specific map", response = MapDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Map with given id does not exist")
	})
	@AuthorizedAccess("MAP_READ")
	MapDto get(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "get specific map", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Map with given id does not exist")
	})
	@AuthorizedAccess("MAP_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@POST
	@Path("/checkOrigin")
	@AuthorizedAccess
	Boolean checkOrigin(OriginChecker originChecker);

}
