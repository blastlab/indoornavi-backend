package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.BuildingConfigurationBean;
import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */
@Path("/building")
@Api("/building")
@Stateless
public class BuildingFacade {

    @EJB
    private BuildingBean buildingBean;

    @Inject
    private BuildingRepository buildingRepository;

    @EJB
    private ComplexBean complexBean;

    @EJB
    private PermissionBean permissionBean;

    @EJB
    private BuildingConfigurationBean buildingConfigurationBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create", response = Building.class)
    public Building create(@ApiParam(value = "building", required = true) Building building) {
        if (building.getComplexId() != null) {

            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplexId(), Permission.UPDATE);
            Complex complex = complexBean.find(building.getComplexId());
            if (complex != null) {
                building.setComplex(complex);
                buildingRepository.save(building);
                return building;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.BuildingInternal.class)
    @ApiOperation(value = "find building")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building with given id wasn't found")
    })
    public Building find(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        //Building building = buildingBean.find(id);
        Building building = buildingRepository.findBy(id);

        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.READ);
            return building;
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @ApiOperation(value = "update building", response = Building.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "complex id or complex empty or doesn't exist")
    })
    public Building update(@ApiParam(value = "building", required = true) Building building) {
        if (building.getId() != null) {
            Complex complex = complexBean.findByBuildingId(building.getId());
            if (complex != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        complex.getId(), Permission.UPDATE);
                building.setComplex(complex);
                buildingRepository.save(building);
                return building;
            }
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete building", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "building with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Building building = buildingRepository.findBy(id);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            //buildingBean.delete(building);
            buildingRepository.remove(building);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/complex/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find buildings by complex id")
    @ApiResponses({
        @ApiResponse(code = 404, message = "complex doesn't exist")
    })
    public List<Building> findAll(@PathParam("id") @ApiParam(value = "complexId", required = true) Long complexId) {
        permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                complexId, Permission.READ);
        if (complexId != null) {
            Complex complex = complexBean.find(complexId);
            if (complex != null) {
                return buildingRepository.findByComplex(complex);
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/{id: \\d+}/config/")
    @ApiOperation(value = "create building's configuration")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist")
    })
    public Response saveConfiguration(@PathParam("id") @ApiParam(value = "buildingId", required = true) Long buildingId) {
        Building building = buildingRepository.findBy(buildingId);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            if (buildingConfigurationBean.saveConfiguration(building)) {
                return Response.noContent().build();
            } else {
                throw new InternalServerErrorException();
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}/config/")
    @ApiOperation(value = "finds building's configuration by id")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
    })
    public String getConfiguration(@PathParam("id") @ApiParam(value = "buildingId", required = true) Long buildingId) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByBuildingAndVersion(buildingId, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{complexName}/{buildingName}/config/")
    @ApiOperation(value = "finds building's configuration by complex name and building name")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration set")
    })
    public String getConfigurationByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{complexName}/{buildingName}/configChecksum/")
    @ApiOperation(value = "finds building's configuration's checksum by complex name and building name")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration checksum set")
    })
    public String getConfigurationChecksumByComplexNameAndBuildingName(
            @PathParam("complexName") @ApiParam(value = "complexName", required = true) String complexName,
            @PathParam("buildingName") @ApiParam(value = "buildingName", required = true) String buildingName) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfigurationChecksum() != null) {
            return buildingConfiguration.getConfigurationChecksum();
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/{id: \\d+}/restoreConfiguration")
    @ApiOperation(value = "restores saved building's configuration")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building doesn't exist or has no configuration saved")
    })
    public Building restoreConfiguration(
            @PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        return buildingConfigurationBean.restoreConfiguration(id);
    }

}
