package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class BuildingConfigurationBean {

    @Inject
    private EntityManager em;

    public void create(BuildingConfiguration buildingConfiguration) {
        em.persist(buildingConfiguration);
    }

    public BuildingConfiguration findByComplexNameAndBuildingNameAndVersion(String complexName, String buildingName, Integer version) {
        return em.createNamedQuery(BuildingConfiguration.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION, BuildingConfiguration.class)
                .setParameter("complexName", complexName)
                .setParameter("buildingName", buildingName)
                .setParameter("version", version)
                .getSingleResult();
    }
    
    public BuildingConfiguration findByBuildingAndVersion(Long buildingId, Integer version) {
        return em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                .setParameter("buildingId", buildingId)
                .setParameter("version", version)
                .getSingleResult();
    }
    
     public boolean saveConfiguration(Building building, Integer version) {
        try {
            String configuration = generateConfigurationFromBuilding(building);
            BuildingConfiguration buildingConfiguration = new BuildingConfiguration();
            buildingConfiguration.setConfiguration(configuration);
            buildingConfiguration.setConfigurationChecksum(
                    new String(MessageDigest.getInstance("MD5")
                            .digest(configuration.getBytes("UTF-8"))
                    )
            );
            buildingConfiguration.setVersion(version);
            buildingConfiguration.setBuilding(building);
            BuildingConfiguration bc = em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                .setParameter("buildingId", building.getId())
                .setParameter("version", version)
                .getSingleResult();
            if (bc != null) {
                buildingConfiguration.setId(bc.getId());
                em.merge(buildingConfiguration);
            } else {
                em.persist(buildingConfiguration);
            }
            return true;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | JsonProcessingException ex) {
            Logger.getLogger(BuildingBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private String generateConfigurationFromBuilding(Building building) throws JsonProcessingException {
        building.getFloors().stream().forEach((floor) -> {
            floor.getWaypoints().stream().forEach((waypoint) -> {
                waypoint.setWaypointVisits(null);
            });
            floor.setVertices(floor.getVertices().stream().filter(
                    vertex -> !vertex.getInactive()).collect(Collectors.toList()));
        });
        building.setGoals(building.getGoals().stream().filter(
                goal -> !goal.getInactive()).collect(Collectors.toList()));
        building.getGoals().stream().forEach((goal) -> {
            goal.setGoalSelections(null);
        });
        building.setBuildingConfigurations(null);
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(building);
    }
}
