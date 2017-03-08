package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

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
	@ApiOperation(value = "create complex", response = ComplexDto.class)
	ComplexDto create(@ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update complex by id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                  @ApiParam(value = "complex", required = true) @Valid ComplexDto complex);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete complex by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "get complex by id", response = ComplexDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto find(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@Path("/{id: \\d+}/buildings")
	@ApiOperation(value = "get complex by id (include buildings)", response = ComplexDto.WithBuildings.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
	})
	ComplexDto.WithBuildings findWithBuildings(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "get complexes for current user", response = ComplexDto.class, responseContainer = "list")
	List<ComplexDto> findForCurrentUser();

//	@GET
//	@Path("/building/{id: \\d+}")
//	@ApiOperation(value = "find complex by building id", response = ComplexDto.class)
//	@ApiResponses({
//		@ApiResponse(code = 404, message = "building id empty or building doesn't exist")
//	})
//	ComplexDto findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long id);
//
//	@GET
//	@Path("/floor/{id: \\d+}")
//	@ApiOperation(value = "find complex by floor id", response = ComplexDto.class)
//	@ApiResponses({
//		@ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
//	})
//	ComplexDto findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id);
//
//	@GET
//	@Path("/person/{id: \\d+}")
//	@ApiOperation(value = "find complexes by person id", response = ComplexDto.class, responseContainer = "List")
//	@ApiResponses({
//		@ApiResponse(code = 404, message = "person id empty, person or complex doesn't exist")
//	})
//	List<ComplexDto> findByPerson(@ApiParam(value = "personId", required = true) @PathParam("id") Long personId);
}