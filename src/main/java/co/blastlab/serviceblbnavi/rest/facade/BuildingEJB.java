package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingConfigurationBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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

    public BuildingDto create(BuildingDto building) {
        if (building.getComplexId() != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplexId(), Permission.UPDATE);
            Complex complex = complexRepository.findBy(building.getComplexId());
            if (complex != null) {
                Building buildingEntity = new Building();
                buildingEntity.setComplex(complex);
                buildingEntity.setName(building.getName());
                buildingEntity.setMinimumFloor(building.getMinimumFloor());
                buildingEntity.setDegree(building.getDegree());
                buildingEntity = buildingRepository.save(buildingEntity);
                return new BuildingDto(buildingEntity);
            }
        }
        throw new EntityNotFoundException();
    }


    public BuildingDto find(Long id) {
        Building buildingEntity = buildingRepository.findBy(id);

        if (buildingEntity != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    buildingEntity.getComplex().getId(), Permission.READ);
            return new BuildingDto(buildingEntity);
        }
        throw new EntityNotFoundException();
    }


    public BuildingDto update(BuildingDto building) {
        if (building.getId() != null) {
            Complex complex = complexRepository.findByBuildingId(building.getId());
            if (complex != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        complex.getId(), Permission.UPDATE);
                Building buildingEntity = buildingRepository.findBy(building.getId());
                if (buildingEntity != null) {
                    buildingEntity.setComplex(complex);
                    buildingEntity.setName(building.getName());
                    buildingEntity.setMinimumFloor(building.getMinimumFloor());
                    buildingEntity.setDegree(building.getDegree());
                    buildingEntity = buildingRepository.save(buildingEntity);
                    return new BuildingDto(buildingEntity);
                }
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


    public List<BuildingDto> findAll(Long complexId) {
        permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                complexId, Permission.READ);
        if (complexId != null) {
            Complex complex = complexRepository.findBy(complexId);
            if (complex != null) {
                List<BuildingDto> buildings = new ArrayList<>();
                buildingRepository.findByComplex(complex).forEach((buildingEntity -> buildings.add(new BuildingDto(buildingEntity))));
                return buildings;
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


    public BuildingDto restoreConfiguration(Long id) {
        return new BuildingDto(buildingConfigurationBean.restoreConfiguration(id));
    }

}