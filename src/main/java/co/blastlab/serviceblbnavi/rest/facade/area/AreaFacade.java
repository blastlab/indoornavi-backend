package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/areas")
@Api("/areas")
@SetOperationId
public interface AreaFacade {
	@POST
	@ApiOperation(value = "create area", response = AreaDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	AreaDto create(@ApiParam(value = "anchor", required = true) @Valid AreaDto area);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update area by id", response = AreaDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor or area with given id does not exist")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	AreaDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                 @ApiParam(value = "area", required = true) @Valid AreaDto area);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete area by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Area id empty or area does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all areas", response = AreaDto.class, responseContainer = "List")
	@AuthorizedAccess("FLOOR_READ")
	List<AreaDto> findAll();

	@GET
	@Path("/{floorId: \\d+}")
	@ApiOperation(value = "find all areas by floor id", response = AreaDto.class, responseContainer = "list")
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor id empty or floor does not exist")
	})
	@AuthorizedAccess("FLOOR_READ")
	List<AreaDto> findAllByFloor(@PathParam("floorId") @Valid @NotNull Long floorId);
}
