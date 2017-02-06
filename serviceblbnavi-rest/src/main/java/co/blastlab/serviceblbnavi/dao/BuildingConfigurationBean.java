package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
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
import java.util.List;
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
    private BuildingBean buildingBean;

    public void create(BuildingConfiguration buildingConfiguration) {
        em.persist(buildingConfiguration);
    }

    public BuildingConfiguration findByComplexNameAndBuildingNameAndVersion(String complexName, String buildingName, Integer version) {
        try {
            return em.createNamedQuery(BuildingConfiguration.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION, BuildingConfiguration.class)
                    .setParameter("complexName", complexName)
                    .setParameter("buildingName", buildingName)
                    .setParameter("version", version)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public BuildingConfiguration findByBuildingAndVersion(Long buildingId, Integer version) {
        return findByBuildingAndVersion(buildingId, version, em);
    }

    public BuildingConfiguration findByBuildingAndVersion(Long buildingId, Integer version, EntityManager em) {
        try {
            return em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                    .setParameter("buildingId", buildingId)
                    .setParameter("version", version)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean saveConfiguration(Building building) {
        //merge developer building to production
        emProduction.merge(building.getComplex());
        buildingBean.removeSQL(building, emProduction);
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
                bc = emProduction.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                        .setParameter("buildingId", building.getId())
                        .setParameter("version", DB_VERSION)
                        .getSingleResult();
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

    public BuildingConfiguration findLatestVersionByBuildingId(Long id) {
        try {
            return em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_SORT_VERSION_FROM_NEWEST, BuildingConfiguration.class)
                    .setParameter("buildingId", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Building restoreConfiguration(Long id) {
        Building building = emProduction.find(Building.class, id);
        if (building == null) {
            throw new EntityNotFoundException();
        }
        buildingBean.removeSQL(building, em);
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

    public List<BuildingConfiguration> findAllByVersion(Integer version, EntityManager em) {
        return em.createNamedQuery(BuildingConfiguration.FIND_BY_VERSION)
                .setParameter("version", version)
                .getResultList();
    }
}
