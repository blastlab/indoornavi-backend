package co.blastlab.serviceblbnavi.rest.facade.configuration;

import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.List;

@Path("/configurations")
@Api("/configurations")
public interface ConfigurationFacade {
	@POST
	@ApiOperation(value = "publish floor's configuration", response = ConfigurationDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor/Anchor id empty or floor/anchor does not exist")
	})
	//	@AuthorizedAccess("CONFIGURATION_CREATE")
	ConfigurationDto publish(@ApiParam(value = "configuration", required = true) @Valid ConfigurationDto configuration) throws IOException;

	@PUT
	@ApiOperation(value = "save draft", response = ConfigurationDto.class)
	//	@AuthorizedAccess("CONFIGURATION_UPDATE")
	ConfigurationDto saveDraft(@ApiParam(value = "configuration", required = true) @Valid ConfigurationDto configuration) throws IOException;

	@Path("/{floorId: \\d+}")
	@GET
	@ApiOperation(value = "get all configurations for specified floor ordered by version descending", response = ConfigurationDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor id empty or floor does not exist")
	})
		//	@AuthorizedAccess("CONFIGURATION_READ")
	List<ConfigurationDto> getAllOrderedByVersionDescending(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId) throws IOException;
}
