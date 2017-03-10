package co.blastlab.serviceblbnavi.rest.facade.complex;

import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/complex")
@Api("/complex")
@TokenAuthorization
public interface ComplexFacade {

	@POST
	@ApiOperation(value = "create complex", response = ComplexDto.class)
	ComplexDto create(@ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@PUT
	@ApiOperation(value = "update complex by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto update(@ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete complex by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find complex by id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto find(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/building/{id: \\d+}")
	@ApiOperation(value = "find complex by building id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id empty or building doesn't exist")
	})
	ComplexDto findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/floor/{id: \\d+}")
	@ApiOperation(value = "find complex by floor id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
	})
	ComplexDto findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/complete/{id: \\d+}")
	@ApiOperation(value = "find complex by id (include buildings)", response = ComplexDto.WithBuildings.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto.WithBuildings findComplete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);
}