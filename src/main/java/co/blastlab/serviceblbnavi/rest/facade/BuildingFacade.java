package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/building")
@Api("/building")
public interface BuildingFacade {

    @POST
    @ApiOperation(value = "create", response = BuildingDto.class)
    BuildingDto create(@ApiParam(value = "building", required = true) BuildingDto building);

    @PUT
    @ApiOperation(value = "update building", response = BuildingDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id or complex empty or doesn't exist")
    })
    BuildingDto update(@ApiParam(value = "building", required = true) BuildingDto building);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete building", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.BuildingInternal.class)
    @ApiOperation(value = "find building", response = BuildingDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building with given id wasn't found")
    })
    BuildingDto find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/complex/{id: \\d+}")
    @ApiOperation(value = "find buildings by complex id")
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex doesn't exist")
    })
    List<BuildingDto> findAll(@PathParam("id") @ApiParam(value = "complexId", required = true) Long complexId);

    @PUT
    @Path("/{id: \\d+}/config/")
    @ApiOperation(value = "create building's configuration")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building doesn't exist")
    })
    Response saveConfiguration(@PathParam("id") @ApiParam(value = "buildingId", required = true) Long buildingId);

    @GET
    @Path("/{id: \\d+}/config/")
    @ApiOperation(value = "finds building's configuration by id")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
    })
    String getConfiguration(@PathParam("id") @ApiParam(value = "buildingId", required = true) Long buildingId);

    @GET
    @Path("/{complexName}/{buildingName}/config/")
    @ApiOperation(value = "finds building's configuration by complex name and building name")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
    })
    String getConfigurationByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName);

    @GET
    @Path("/{complexName}/{buildingName}/configChecksum/")
    @ApiOperation(value = "finds building's configuration's checksum by complex name and building name")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building doesn't exist or has no configuration checksum set")
    })
    String getConfigurationChecksumByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName);

    @PUT
    @Path("/{id: \\d+}/restoreConfiguration")
    @ApiOperation(value = "restores saved building's configuration")
    @ApiResponses({
            @ApiResponse(code = 404, message = "building doesn't exist or has no configuration saved")
    })
    BuildingDto restoreConfiguration(@PathParam("id") @ApiParam(value = "id", required = true) Long id);
}