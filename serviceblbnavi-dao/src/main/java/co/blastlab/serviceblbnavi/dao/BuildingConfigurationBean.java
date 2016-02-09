package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import javax.persistence.EntityNotFoundException;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class BuildingConfigurationBean {

    @Inject
    private EntityManager em;

    @Inject
    @NaviProduction
    private EntityManager emProduction;

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
        emProduction.merge(building);
        try {
            String configuration = generateConfigurationFromBuilding(building);
            BuildingConfiguration buildingConfiguration = new BuildingConfiguration();
            buildingConfiguration.setConfiguration(configuration);
            buildingConfiguration.setConfigurationChecksum(
                    new String(MessageDigest.getInstance("MD5")
                            .digest(configuration.getBytes("UTF-8"))
                    )
            );
            buildingConfiguration.setVersion(UpgradeBean.DB_VERSION);
            buildingConfiguration.setBuilding(building);
            BuildingConfiguration bc;
            try {
                bc = emProduction.createNamedQuery(BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, BuildingConfiguration.class)
                        .setParameter("buildingId", building.getId())
                        .setParameter("version", UpgradeBean.DB_VERSION)
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
        Building buildingInDB = em.find(Building.class, id);
        Complex complex = buildingInDB.getComplex();
        List<BuildingConfiguration> buildingConfigurations = new ArrayList<>();
        buildingConfigurations.addAll(building.getBuildingConfigurations());

        buildingBean.removeSQL(buildingInDB, em);

        List<Edge> edges = new ArrayList<>();
        List<Vertex> vertices = new ArrayList<>();
        building.getFloors().forEach((floor) -> {
            floor.setBuilding(building);
            floor.getVertices().forEach((vertex) -> {
                vertex.setFloor(floor);
                edges.addAll(vertex.getSourceEdges());
                vertex.setSourceEdges(new ArrayList<>());
                vertex.setTargetEdges(new ArrayList<>());
            });
            vertices.addAll(floor.getVertices());
            floor.getBeacons().forEach((beacon) -> {
                beacon.setFloor(floor);
            });
            floor.getWaypoints().forEach((waypoint) -> {
                waypoint.setFloor(floor);
            });
            floor.getGoals().forEach((goal) -> {
                goal.setFloor(floor);
            });
        });
        building.setComplex(complex);
        building.setBuildingConfigurations(buildingConfigurations);
        edges.forEach((edge) -> {
            edge.setSource(vertices.stream().filter((vertex) -> {
                return vertex.getId().equals(edge.getSourceId());
            }).collect(Collectors.toList()).get(0));
            edge.setTarget(vertices.stream().filter((vertex) -> {
                return vertex.getId().equals(edge.getTargetId());
            }).collect(Collectors.toList()).get(0));
        });
        buildingBean.insertSQL(building, em);
        building.getFloors().forEach((floor) -> {
            floorBean.insertSQL(floor, em);
            floor.getBeacons().forEach((beacon) -> {
                beaconBean.insertSQL(beacon, em);
            });
            floor.getWaypoints().forEach((waypoint) -> {
                waypointBean.insertSQL(waypoint, em);
            });
            floor.getVertices().forEach((vertex) -> {
                vertexBean.insertSQL(vertex, em);
            });
            floor.getGoals().forEach((goal) -> {
                goalBean.insertSQL(goal, em);
            });
        });
        building.getBuildingConfigurations().forEach((bc) -> {
            insertSQL(bc, em);
        });
        edges.forEach((edge) -> {
            edgeBean.insertSQL(edge, em);
        });
        return building;
    }

    private void insertSQL(BuildingConfiguration bc, EntityManager em) {
        em.createNativeQuery("INSERT INTO BuildingConfiguration (id, building_id, version, configuration, configurationChecksum) VALUES (:id, :building_id, :version, :configuration, :configurationChecksum)")
                .setParameter("id", bc.getId())
                .setParameter("building_id", bc.getBuilding().getId())
                .setParameter("version", bc.getVersion())
                .setParameter("configuration", bc.getConfiguration())
                .setParameter("configurationChecksum", bc.getConfigurationChecksum())
                .executeUpdate();
    }

    public List<BuildingConfiguration> findAllByVersion(Integer version, EntityManager em) {
        return em.createNamedQuery(BuildingConfiguration.FIND_BY_VERSION)
                .setParameter("version", version)
                .getResultList();
    }
}
