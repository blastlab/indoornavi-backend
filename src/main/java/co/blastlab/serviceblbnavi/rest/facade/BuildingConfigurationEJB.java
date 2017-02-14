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

@Stateless
public class BuildingConfigurationEJB implements BuildingConfigurationFacade {

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private ComplexRepository complexRepository;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private BuildingConfigurationBean buildingConfigurationBean;


    public Response create(String complexName, String buildingName, Integer version) {
        Complex complex = complexRepository.findOptionalByName(complexName);
        if (complex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    complex.getId(), Permission.UPDATE);
            Building building = buildingRepository.findByComplexNameAndBuildingName(complexName, buildingName);
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
    

    public String getConfigurationByComplexNameAndBuildingName(String complexName, String buildingName, Integer version) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, version);
        if (buildingConfiguration != null && buildingConfiguration.getConfiguration() != null) {
            return buildingConfiguration.getConfiguration();
        }
        throw new EntityNotFoundException();
    }


    public String getConfigurationChecksumByComplexNameAndBuildingName(String complexName,String buildingName, Integer version) {
        BuildingConfiguration buildingConfiguration = buildingConfigurationBean.findByComplexNameAndBuildingNameAndVersion(complexName, buildingName, version);
        if (buildingConfiguration != null && buildingConfiguration.getConfigurationChecksum()!= null) {
            return buildingConfiguration.getConfigurationChecksum();
        }
        throw new EntityNotFoundException();
    }
}
