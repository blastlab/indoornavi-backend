package co.blastlab.serviceblbnavi.rest.facade.complex;

import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/complexes")
@Api("/complexes")
@TokenAuthorization
public interface ComplexFacade {

	@POST
	@ApiOperation(value = "create complex", response = ComplexDto.WithId.class)
	ComplexDto.WithId create(@ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update complex by id", notes = "update complex by idss", response = ComplexDto.WithId.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto.WithId update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                         @ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete complex by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all complexes", response = ComplexDto.WithId.class, responseContainer = "List")
	List<ComplexDto.WithId> findAll();

	@GET
	@Path("/{id: \\d+}/buildings")
	@ApiOperation(value = "get complex by id (include buildings)", response = ComplexDto.WithId.WithBuildings.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto.WithId.WithBuildings findWithBuildings(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);
}