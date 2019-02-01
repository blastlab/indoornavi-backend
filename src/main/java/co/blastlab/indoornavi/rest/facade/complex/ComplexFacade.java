package co.blastlab.indoornavi.rest.facade.complex;

import co.blastlab.indoornavi.dto.complex.ComplexDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/complexes")
@Api("/complexes")
@SetOperationId
public interface ComplexFacade {

	@POST
	@ApiOperation(value = "create complex", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 400, message = "Complex name is empty")
	})
	@AuthorizedAccess("COMPLEX_CREATE")
	ComplexDto create(@ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update complex by id", notes = "update complex by id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Complex id empty or complex does not exist")
	})
	@AuthorizedAccess("COMPLEX_UPDATE")
	ComplexDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                         @ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete complex by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Complex id empty or complex does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("COMPLEX_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all complexes", response = ComplexDto.class, responseContainer = "List")
	@AuthorizedAccess("COMPLEX_READ")
	List<ComplexDto.WithBuildings.WithFloors> findAll();

	@GET
	@Path("/{id: \\d+}/buildings")
	@ApiOperation(value = "get complex by id (include buildings)", response = ComplexDto.WithBuildings.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Complex id empty or complex does not exist")
	})
	@AuthorizedAccess("COMPLEX_READ")
	ComplexDto.WithBuildings findWithBuildings(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);
}
