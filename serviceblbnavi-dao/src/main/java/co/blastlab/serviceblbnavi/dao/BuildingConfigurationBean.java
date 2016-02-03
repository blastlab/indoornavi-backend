package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.GoalSelection;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.InternalServerErrorException;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class BuildingConfigurationBean {

    @Inject
    private EntityManager em;

    @EJB
    private BuildingBean buildingBean;

    @EJB
    private FloorBean floorBean;

    @EJB
    private BeaconBean beaconBean;

    @EJB
    private WaypointBean waypointBean;
    
    @EJB
    private VertexBean vertexBean;
    
    @EJB
    private GoalBean goalBean;
    
    @EJB
    private EdgeBean edgeBean;
    
    @EJB
    private GoalSelectionBean goalSelectionBean;
    
    @EJB
    private WaypointVisitBean waypointVisitBean;

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
            BuildingConfiguration bc;
            try {
                bc = em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                        .setParameter("buildingId", building.getId())
                        .setParameter("version", version)
                        .getSingleResult();
            } catch (NoResultException e) {
                bc = null;
            }
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

    public BuildingConfiguration findLatestVersionByBuildingId(Long id) {
        try {
            return em.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_SORT_VERSION_FROM_NEWEST, BuildingConfiguration.class)
                    .setParameter("buildingId", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Building restoreConfiguration(BuildingConfiguration buildingConfiguration, Long id) {
        try {
            Building building = new ObjectMapper().readValue(buildingConfiguration.getConfiguration(), Building.class);
            Building buildingInDB = em.find(Building.class, building.getId());
            Complex complex = buildingInDB.getComplex();
            List<BuildingConfiguration> buildingConfigurations = new ArrayList<>();
            buildingConfigurations.addAll(buildingInDB.getBuildingConfigurations());
            List<WaypointVisit> waypointVisits = new ArrayList<>();
            List<GoalSelection> goalSelections = new ArrayList<>();
            buildingInDB.getFloors().forEach((floor) -> {
                floor.getWaypoints().forEach((waypoint) -> {
                    waypointVisits.addAll(waypoint.getWaypointVisits());
                });
                floor.getVertices().forEach((vertex) -> {
                    vertex.getGoals().forEach((goal) -> {
                        goalSelections.addAll(goal.getGoalSelections());
                    });
                });
            });
            building.getFloors().forEach((floor) -> {
                Floor otherFloor = em.find(Floor.class, floor.getId());
                if (otherFloor != null) {
                    /* floor was not deleted, copy bitmap */
                    floor.setBitmap(otherFloor.getBitmap());
                }
            });
            buildingBean.removeSQL(buildingInDB);

            List<Edge> edges = new ArrayList<>();
            List<Vertex> vertices = new ArrayList<>();
            building.getFloors().forEach((floor) -> {
                floor.setBuilding(building);
                floor.getVertices().forEach((vertex) -> {
                    vertex.setFloor(floor);
                    edges.addAll(vertex.getSourceEdges());
                    vertex.setSourceEdges(new ArrayList<>());
                    vertex.setTargetEdges(new ArrayList<>());
                    vertex.getGoals().forEach((goal) -> {
                        goal.setVertex(vertex);
                        goal.setBuilding(building);
                    });
                });
                vertices.addAll(floor.getVertices());
                floor.getBeacons().forEach((beacon) -> {
                    beacon.setFloor(floor);
                });
                floor.getWaypoints().forEach((waypoint) -> {
                    waypoint.setFloor(floor);
                });
            });
            building.setComplex(complex);
            building.setBuildingConfigurations(buildingConfigurations);
            System.out.println("Building id: " + building.getId());
            edges.forEach((edge) -> {
                edge.setSource(vertices.stream().filter((vertex) -> {
                    return vertex.getId().equals(edge.getSourceId());
                }).collect(Collectors.toList()).get(0));
                edge.setTarget(vertices.stream().filter((vertex) -> {
                    return vertex.getId().equals(edge.getTargetId());
                }).collect(Collectors.toList()).get(0));
            });
            System.out.println("Building id: " + building.getId());
            buildingBean.insertSQL(building);
            building.getFloors().forEach((floor) -> {
                floorBean.insertSQL(floor);
                floor.getBeacons().forEach((beacon) -> {
                    beaconBean.insertSQL(beacon);
                });
                floor.getWaypoints().forEach((waypoint) -> {
                    waypointBean.insertSQL(waypoint);
                });
                floor.getVertices().forEach((vertex) -> {
                    vertexBean.insertSQL(vertex);
                    vertex.getGoals().forEach((goal) -> {
                        goalBean.insertSQL(goal);
                    });
                });
            });
            building.getBuildingConfigurations().forEach((bc) -> {
                insertSQL(bc);
            });
            edges.forEach((edge) -> {
                edgeBean.insertSQL(edge);
            });
            goalSelections.forEach((goalSelection) -> {
                goalSelectionBean.insetSQL(goalSelection);
            });
            waypointVisits.forEach((waypointVisit) -> {
                waypointVisitBean.insertSQL(waypointVisit);
            });
            return building;
        } catch (IOException ex) {
            throw new InternalServerErrorException(ex);
        }
    }

    private void insertSQL(BuildingConfiguration bc) {
        em.createNativeQuery("INSERT INTO BuildingConfiguration (id, building_id, version, configuration, configurationChecksum) VALUES (:id, :building_id, :version, :configuration, :configurationChecksum)")
                .setParameter("id", bc.getId())
                .setParameter("building_id", bc.getBuilding().getId())
                .setParameter("version", bc.getVersion())
                .setParameter("configuration", bc.getConfiguration())
                .setParameter("configurationChecksum", bc.getConfigurationChecksum())
                .executeUpdate();
    }
}
