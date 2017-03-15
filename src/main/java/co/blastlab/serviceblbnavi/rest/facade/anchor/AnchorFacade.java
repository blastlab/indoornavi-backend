package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/anchor")
@Api("/anchor")
public interface AnchorFacade {

	@POST
	@ApiOperation(value = "create", response = AnchorDto.WithFloor.WithId.class)
	AnchorDto.WithFloor.WithId create(@ApiParam(value = "anchor", required = true) @Valid AnchorDto anchor);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "set floor for anchor", response = AnchorDto.WithFloor.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor or anchor with given id does not exist")
	})
	AnchorDto.WithFloor.WithId setFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id,
	                                    @ApiParam(value = "anchor", required = true) @Valid AnchorDto.WithFloor anchor);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find anchor by longId", response = AnchorDto.WithFloor.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Id empty or anchor does not exist")
	})
	AnchorDto.WithFloor.WithId find(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/floor/{id: \\d+}")
	@ApiOperation(value = "find anchors by floor", response = AnchorDto.WithFloor.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
	})
	List<AnchorDto.WithFloor.WithId> findByFloor(@ApiParam(value = "floor id", required = true) @PathParam("id") Long floorId);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete anchor by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "anchor id empty or anchor doesn't exist")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);
}
