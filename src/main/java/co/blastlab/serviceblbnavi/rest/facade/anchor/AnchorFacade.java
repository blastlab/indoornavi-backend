package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/anchors")
@Api("/anchors")
public interface AnchorFacade {

	@POST
	@ApiOperation(value = "create", response = AnchorDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	AnchorDto.WithId create(@ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "set floor for anchor", response = AnchorDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor or anchor with given id does not exist")
	})
	AnchorDto.WithId update(@ApiParam(value = "id", required = true) @PathParam("id") Long id,
	                        @ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@GET
	@ApiOperation(value = "find all anchors", response = AnchorDto.WithId.class)
	List<AnchorDto.WithId> findAll();

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete anchor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "anchor id empty or anchor doesn't exist")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);
}
