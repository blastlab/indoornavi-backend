package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;

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

    public Building find(Long id) {
        return em.find(Building.class, id);
    }

    public Building findByComplexNameAndBuildingName(String complexName, String buildingName) {
        return em.createNamedQuery(Building.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME, Building.class)
                .setParameter("complexName", complexName)
                .setParameter("buildingName", buildingName)
                .getSingleResult();
    }

    public void removeSQL(Building building, EntityManager em) {
        em.createNativeQuery("DELETE FROM Building WHERE id = :id")
                .setParameter("id", building.getId())
                .executeUpdate();
    }

}
