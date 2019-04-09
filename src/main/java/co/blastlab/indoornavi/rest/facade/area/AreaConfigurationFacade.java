package co.blastlab.indoornavi.rest.facade.area;

import co.blastlab.indoornavi.dto.area.AreaConfigurationDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/areaConfigurations")
@Api("/areaConfigurations")
@SetOperationId
public interface AreaConfigurationFacade {
	@POST
	@ApiOperation(value = "create area configuration", response = AreaConfigurationDto.class)
	@AuthorizedAccess("FLOOR_UPDATE")
	AreaConfigurationDto create(@ApiParam(value = "areaConfiguration", required = true) @Valid AreaConfigurationDto areaConfiguration);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update area configuration by id", response = AreaConfigurationDto.class)
	@AuthorizedAccess("FLOOR_UPDATE")
	AreaConfigurationDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	               @ApiParam(value = "areaConfiguration", required = true) @Valid AreaConfigurationDto areaConfiguration);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete area configuration by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Area configuration id empty or area configuration does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all area configurations", response = AreaConfigurationDto.class, responseContainer = "List")
	@AuthorizedAccess("FLOOR_READ")
	List<AreaConfigurationDto> findAll();
}
