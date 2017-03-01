package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.vertex.VertexDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/vertex")
@Api("/vertex")
@TokenAuthorization
public interface VertexFacade {

	@POST
	@ApiOperation(value = "create vertex", response = VertexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor id empty or floor doesn't exist"),
		@ApiResponse(code = 400, message = "X or Y less than 0")
	})
	VertexDto create(@ApiParam(value = "vertex", required = true) @Valid VertexDto vertex);

	@PUT
	@ApiOperation(value = "update vertex coordinates", response = VertexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "vertex id empty or doesn't exist"),
		@ApiResponse(code = 400, message = "X or Y less than 0")
	})
	VertexDto update(@ApiParam(value = "vertex", required = true) @Valid VertexDto vertex);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete vertex", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "vertex with given id doesn't exist")
	})
	Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

	@GET
	@Path("/floor/{id: \\d+}")
	@ApiOperation(value = "find vertices for specified floor", response = VertexDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id wasn't found")
	})
	List<VertexDto> findByFloor(@ApiParam(value = "floor id", required = true) @PathParam("id") Long floorId);

	@GET
	@Path("/floor/{id: \\d+}/active")
	@ApiOperation(value = "find active vertices for specified floor", response = VertexDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id wasn't found")
	})
	List<VertexDto> findAllActiveByFloor(@ApiParam(value = "floor id", required = true) @PathParam("id") Long floorId);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find vertex by id", response = VertexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "vertex with given id wasn't found")
	})
	VertexDto findById(@ApiParam(value = "vertex id", required = true) @PathParam("id") Long vertexId);

	@PUT
	@Path("/{id: \\d+}/deactivate")
	@ApiOperation(value = "deactivates vertex of given id", response = VertexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "vertex with given id wasn't found")
	})
	VertexDto deactivate(@ApiParam(value = "vertex id", required = true) @PathParam("id") Long vertexId);
}