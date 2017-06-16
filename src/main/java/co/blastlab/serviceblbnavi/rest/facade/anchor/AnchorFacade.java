package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/anchors")
@Api("/anchors")
public interface AnchorFacade {

	@POST
	@ApiOperation(value = "create anchor", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	AnchorDto create(@ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update anchor by id", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor or anchor with given id does not exist")
	})
	AnchorDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                 @ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete anchor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Anchor id empty or anchor does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all anchors", response = AnchorDto.class, responseContainer = "List")
	List<AnchorDto> findAll();
}