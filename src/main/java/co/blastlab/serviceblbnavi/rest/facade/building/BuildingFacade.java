package co.blastlab.serviceblbnavi.rest.facade.building;

import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/building")
@Api("/building")
public interface BuildingFacade {

	@POST
	@TokenAuthorization
	@ApiOperation(value = "create", response = BuildingDto.New.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id or complex empty or doesn't exist")
	})
	BuildingDto create(@ApiParam(value = "building", required = true) @Valid BuildingDto.New building);

	@PUT
	@TokenAuthorization
	@ApiOperation(value = "update building", response = BuildingDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building doesn't exist or complex doesn't contain given building")
	})
	BuildingDto update(@ApiParam(value = "building", required = true) @Valid BuildingDto building);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete building", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id doesn't exist")
	})
	@TokenAuthorization
	Response delete(@PathParam("id") @ApiParam(value = "building id", required = true) Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find building", response = BuildingDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id wasn't found")
	})
	@TokenAuthorization
	BuildingDto find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

	@GET
	@Path("/complex/{id: \\d+}")
	@ApiOperation(value = "find buildings by complex id", response = BuildingDto.class, responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex doesn't exist")
	})
	@TokenAuthorization
	List<BuildingDto> findAll(@PathParam("id") @ApiParam(value = "complexId", required = true) Long complexId);
}