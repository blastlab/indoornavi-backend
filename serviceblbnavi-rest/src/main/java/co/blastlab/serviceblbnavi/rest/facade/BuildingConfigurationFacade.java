package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.BuildingConfigurationBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import com.wordnik.swagger.annotations.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/buildingConfiguration")
@Api("/buildingConfiguration")
public class BuildingConfigurationFacade {

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private ComplexRepository complexRepository;

    @EJB
    private BuildingBean buildingBean;

    @Inject
    private BuildingRepository buildingRepository;

    @EJB
    private BuildingConfigurationBean buildingConfigurationBean;

    @POST
    @Path("/{complexName}/{buildingName}/{version: \\d+}")
    @ApiOperation(value = "creates building configuration")
    public Response create(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
            @PathParam("version") @ApiParam(value = "version", required = true) Integer version) {
        Complex complex = complexRepository.findOptionalByName(complexName);
        if (complex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    complex.getId(), Permission.UPDATE);
            Building building = buildingBean.findByComplexNameAndBuildingName(complexName, buildingName);
            if (building != null) {
                if (buildingConfigurationBean.saveConfiguration(building)) {
                    return Response.noContent().build();
                } else {
                    throw new InternalServerErrorException();
                }
            }
        }
        throw new EntityNotFoundException();
    }
    
    
    @GET
    @Path("/{complexName}/{buildingName}/{version: \\d+}/")
    @ApiOperation(value = "finds building's configuration by complex name, building name and version")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
    })
    public String getConfigurationByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
            @PathParam("version") @ApiParam(value = "version", required = true) Integer version) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, version);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{complexName}/{buildingName}/{version: \\d+}/checksum")
    @ApiOperation(value = "finds building's configuration's checksum by complex name, building name and version")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration checksum set")
    })
    public String getConfigurationChecksumByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName,
            @PathParam("version") @ApiParam(value = "version", required = true) Integer version) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, version);
        if (buildingConfiguration != null && buildingConfiguration.getConfigurationChecksum()!= null) {
            return buildingConfiguration.getConfigurationChecksum();
        }
        throw new EntityNotFoundException();
    }
}
