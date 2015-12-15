package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class BuildingBean {

    @Inject
    private EntityManager em;

    public void create(Building building) {
        em.persist(building);
    }

    public Building find(Long id) {
        return em.find(Building.class, id);
    }

    public List<Building> findByComplex(Complex complex) {
        return em.createNamedQuery(Building.FIND_BY_COMPLEX, Building.class)
                .setParameter("complex", complex).getResultList();
    }

    public void delete(Building building) {
        em.remove(em.contains(building) ? building : em.merge(building));
    }

    public void update(Building building) {
        em.merge(building);
    }

    public boolean saveConfiguration(Building building) {
        try {
            String configuration = generateConfigurationFromBuilding(building);
            building.setConfiguration(configuration);
            building.setConfigurationChecksum(
                    new String(MessageDigest.getInstance("MD5")
                            .digest(configuration.getBytes("UTF-8"))
                    )
            );
            update(building);
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
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(building);
    }

    public Building findByComplexNameAndBuildingName(String complexName, String buildingName) {
        return em.createNamedQuery(Building.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME, Building.class)
                .setParameter("complexName", complexName)
                .setParameter("buildingName", buildingName)
                .getSingleResult();
    }

}
