package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;

@Path("/anchor")
@Api("/anchor")
public interface AnchorFacade {

	@POST
	@ApiOperation(value = "create", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "")
	})
	AnchorDto create(@ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@PUT
	@ApiOperation(value = "set floor for anchor", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floorId empty or floor or anchor with given id does not exist")
	})
	AnchorDto setFloor(@ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@PUT
	@Path("/{longId: (.+)?}//{shortId: (.+)?}")
	@ApiOperation(value = "set shortId for anchor", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "anchor with given id does not exist")
	})
	AnchorDto setShortId(@ApiParam(value = "longId", required = true) @PathParam("longId") String longId,
	                     @ApiParam(value = "shortId", required = true) @PathParam("shortId") String shortId);

	@GET
	@Path("/{longId: (.+)?}")
	@ApiOperation(value = "find anchor by longId", response = AnchorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "anchor longId empty or anchor doesn't exist")
	})
	AnchorDto findByLongId(@ApiParam(value = "longId", required = true) @PathParam("longId") String longId);

}
