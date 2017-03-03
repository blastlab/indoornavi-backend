package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.edge.EdgeDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/edge")
@Api("/edge")
@TokenAuthorization
public interface EdgeFacade {

	@POST
	@ApiOperation(value = "create edges", response = EdgeDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "target or source id (vertex) empty or doesn't exist")
	})
	List<EdgeDto> create(@ApiParam(value = "edges", required = true) @Valid List<EdgeDto> edges);

	@PUT
	@ApiOperation(value = "update edges' weight", response = EdgeDto.class, responseContainer = "List")
	List<EdgeDto> update(@ApiParam(value = "edges", required = true) @Valid List<EdgeDto> edges);

	@DELETE
	@ApiOperation(value = "delete edge", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
	})
	Response delete(
		@ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
		@ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find edges by floor id", response = EdgeDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id wasn't found")
	})
	List<EdgeDto> findByVertexFloorId(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

	@GET
	@Path("/vertex/{id: \\d+}")
	@ApiOperation(value = "find edges by vertex id", response = EdgeDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "vertex with given id wasn't found")
	})
	List<EdgeDto> findByVertexId(@PathParam("id") @ApiParam(value = "vertex id", required = true) Long vertexId);

	@GET
	@ApiOperation(value = "find edge", response = EdgeDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
	})
	EdgeDto findBySourceIdAndTargetId(
		@ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
		@ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId);
}
