package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingConfigurationBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
public class BuildingEJB implements BuildingFacade {

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private ComplexRepository complexRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private BuildingConfigurationBean buildingConfigurationBean;

    @Inject
    private AuthorizationBean authorizationBean;

    public Building create(Building building) {
        if (building.getComplexId() != null) {

            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplexId(), Permission.UPDATE);
            Complex complex = complexRepository.findBy(building.getComplexId());
            if (complex != null) {
                building.setComplex(complex);
                buildingRepository.save(building);
                return building;
            }
        }
        throw new EntityNotFoundException();
    }


    public Building find(Long id) {
        Building building = buildingRepository.findBy(id);

        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.READ);
            return building;
        }
        throw new EntityNotFoundException();
    }


    public Building update(Building building) {
        if (building.getId() != null) {
            Complex complex = complexRepository.findByBuildingId(building.getId());
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


    public Response delete(Long id) {
        Building building = buildingRepository.findBy(id);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            buildingRepository.remove(building);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public List<Building> findAll(Long complexId) {
        permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                complexId, Permission.READ);
        if (complexId != null) {
            Complex complex = complexRepository.findBy(complexId);
            if (complex != null) {
                return buildingRepository.findByComplex(complex);
            }
        }
        throw new EntityNotFoundException();
    }


    public Response saveConfiguration(Long buildingId) {
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


    public String getConfiguration(Long buildingId) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByBuildingAndVersion(buildingId, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }


    public String getConfigurationByComplexNameAndBuildingName(String complexName, String buildingName) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }


    public String getConfigurationChecksumByComplexNameAndBuildingName(String complexName, String buildingName) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, 1);
        if (buildingConfiguration != null && buildingConfiguration.getConfigurationChecksum() != null) {
            return buildingConfiguration.getConfigurationChecksum();
        }
        throw new EntityNotFoundException();
    }


    public Building restoreConfiguration(Long id) {
        return buildingConfigurationBean.restoreConfiguration(id);
    }

}