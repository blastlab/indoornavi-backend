package co.blastlab.indoornavi.rest.facade.configuration;

import co.blastlab.indoornavi.dto.configuration.ConfigurationDto;
import co.blastlab.indoornavi.dto.configuration.PrePublishReport;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.List;

@Path("/configurations")
@Api("/configurations")
@SetOperationId
public interface ConfigurationFacade {
	@Path("/{floorId: \\d+}/pre")
	@POST
	@ApiOperation(value = "pre publish to get a report about what will happen after you publish", response = PrePublishReport.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor not found in a database")
	})
	PrePublishReport prePublish(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId) throws IOException;

	@Path("/{floorId: \\d+}")
	@POST
	@ApiOperation(value = "publish floor's configuration", response = ConfigurationDto.Data.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor/Anchor id empty or floor/anchor does not exist")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	ConfigurationDto.Data publish(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId) throws IOException;

	@PUT
	@ApiOperation(value = "save draft", response = ConfigurationDto.Data.class)
	@AuthorizedAccess("FLOOR_UPDATE")
	ConfigurationDto.Data saveDraft(@ApiParam(value = "configuration", required = true) @Valid ConfigurationDto configuration) throws IOException;

	@Path("/{floorId: \\d+}")
	@DELETE
	@ApiOperation(value = "undo publication", response = ConfigurationDto.class)
	@AuthorizedAccess("FLOOR_UPDATE")
	ConfigurationDto undo(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId) throws IOException;

	@Path("/{floorId: \\d+}")
	@GET
	@ApiOperation(value = "get all configurations for specified floor ordered by version descending", response = ConfigurationDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor id empty or floor does not exist")
	})
	@AuthorizedAccess("FLOOR_UPDATE")
	List<ConfigurationDto> getAllOrderedByVersionDescending(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId) throws IOException;
}
