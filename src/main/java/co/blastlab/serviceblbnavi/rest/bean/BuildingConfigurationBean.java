package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class BuildingConfigurationBean {

    public final static Integer DB_VERSION = 2;

    @Inject
    private EntityManager em;

    @Inject
    @NaviProduction
    private EntityManager emProduction;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private ComplexRepository complexRepository;

    @Inject
    private BuildingConfigurationRepository buildingConfigurationRepository;

    @Inject
    private BuildingConfigurationProductionRepository buildingConfigurationProductionRepository;

    @Inject
    private BuildingProductionRepository buildingProductionRepository;

    public BuildingConfiguration findByComplexNameAndBuildingNameAndVersion(String complexName, String buildingName, Integer version) {
        Complex complex = complexRepository.findOptionalByName(complexName);
        Building building = buildingRepository.findOptionalByComplexAndName(complex, buildingName);
        BuildingConfiguration buildingConfiguration = buildingConfigurationRepository.findOptionalByBuildingAndVersion(building, version);
        return buildingConfiguration;
    }

    public boolean saveConfiguration(Building building) {
        //merge developer building to production
        emProduction.merge(building.getComplex());
        emProduction.remove(emProduction.contains(building) ? building : emProduction.merge(building));
        emProduction.merge(building);
        building.getFloors().forEach((floor) -> {
            emProduction.merge(floor);
            floor.getBeacons().forEach((beacon) -> {
                emProduction.merge(beacon);
            });
            floor.getVertices().forEach((vertex) -> {
                emProduction.merge(vertex);
                vertex.getBuildingExits().forEach((buildingExit) -> {
                    em.merge(buildingExit);
                });
            });
            floor.getGoals().forEach((goal) -> {
                emProduction.merge(goal);
            });
            floor.getWaypoints().forEach((waypoint) -> {
                emProduction.merge(waypoint);
            });
        });
        building.getFloors().forEach((floor) -> {
            floor.getVertices().forEach((vertex) -> {
                vertex.getSourceEdges().forEach((edge) -> {
                    emProduction.merge(edge);
                });
                vertex.getBuildingExits().forEach((buildingExit) -> {
                    buildingExit.getSourceConnections().forEach((connection) -> {
                        emProduction.merge(connection);
                    });
                });
                
            });
        });
        
        try {
            String configuration = generateConfigurationFromBuilding(building);
            BuildingConfiguration buildingConfiguration = new BuildingConfiguration();
            buildingConfiguration.setConfiguration(configuration);
            buildingConfiguration.setConfigurationChecksum(
                    new String(MessageDigest.getInstance("MD5")
                            .digest(configuration.getBytes("UTF-8"))
                    )
            );
            buildingConfiguration.setVersion(DB_VERSION);
            buildingConfiguration.setBuilding(building);
            BuildingConfiguration bc;
            try {
                bc = buildingConfigurationProductionRepository.findByBuildingAndVersion(building, DB_VERSION);
            } catch (NoResultException e) {
                bc = null;
            }
            if (bc != null) {
                buildingConfiguration.setId(bc.getId());
                emProduction.merge(buildingConfiguration);
            } else {
                emProduction.persist(buildingConfiguration);
            }
            return true;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | JsonProcessingException ex) {
            Logger.getLogger(BuildingConfigurationBean.class.getName()).log(Level.SEVERE, null, ex);
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
            floor.setGoals(floor.getGoals().stream().filter(
                    goal -> !goal.getInactive()).collect(Collectors.toList()));
            floor.getGoals().forEach((goal) -> {
                goal.setGoalSelections(null);
            });
        });
        building.setBuildingConfigurations(null);
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(building);
    }

    public Building restoreConfiguration(Long id) {
        Building building = emProduction.find(Building.class, id);
        if (building == null) {
            throw new EntityNotFoundException();
        }
        em.remove(em.contains(building) ? building : em.merge(building));
        buildingRepository.attachAndRemove(building);
        em.merge(building);
        building.getFloors().forEach((floor) -> {
            em.merge(floor);
            floor.getBeacons().forEach((beacon) -> {
                em.merge(beacon);
            });
            floor.getWaypoints().forEach((waypoint) -> {
                em.merge(waypoint);
            });
            floor.getVertices().forEach((vertex) -> {
                em.merge(vertex);
            });
            floor.getGoals().forEach((goal) -> {
                em.merge(goal);
            });
        });
        building.getFloors().forEach((floor) -> {
             floor.getVertices().forEach((vertex) -> {
                vertex.getSourceEdges().forEach((edge) -> {
                    em.merge(edge);
                });
            });
        });
        return building;
    }
}
