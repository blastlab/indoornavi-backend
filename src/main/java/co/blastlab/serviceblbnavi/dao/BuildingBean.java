package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class BuildingBean {

    public void removeSQL(Building building, EntityManager em) {
        em.createNativeQuery("DELETE FROM Building WHERE id = :id")
                .setParameter("id", building.getId())
                .executeUpdate();
    }

}
