package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/buildingConfiguration")
@Api("/buildingConfiguration")
public interface BuildingConfigurationFacade {

	@POST
	@Path("/{complexName}/{buildingName}/{version: \\d+}")
	@ApiOperation(value = "creates building configuration")
	@ApiResponses({
		@ApiResponse(code = 404, message = "Complex doesn't exist or doesn't contain given building")
	})
	@TokenAuthorization
	Response create(
		@PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
		@PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
		@PathParam("version") @ApiParam(value = "version", required = true) Integer version);

	@GET
	@Path("/{complexName}/{buildingName}/{version: \\d+}/")
	@ApiOperation(value = "finds building's configuration by complex name, building name and version", response = String.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
	})
	String getConfigurationByComplexNameAndBuildingName(
		@PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
		@PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
		@PathParam("version") @ApiParam(value = "version", required = true) Integer version);

	@GET
	@Path("/{complexName}/{buildingName}/{version: \\d+}/checksum")
	@ApiOperation(value = "finds building's configuration's checksum by complex name, building name and version")
	@ApiResponses({
		@ApiResponse(code = 404, message = "building doesn't exist or has no configuration checksum set")
	})
	String getConfigurationChecksumByComplexNameAndBuildingName(
		@PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
		@PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
		@PathParam("version") @ApiParam(value = "version", required = true) Integer version);
}