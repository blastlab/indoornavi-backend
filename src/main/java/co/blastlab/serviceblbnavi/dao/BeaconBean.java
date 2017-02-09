package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Beacon;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class BeaconBean {

    @Inject
    private EntityManager em;

    public void create(Beacon beacon) {
        em.persist(beacon);
    }

    public Beacon find(Long id) {
        return em.find(Beacon.class, id);
    }

    public void delete(Beacon beacon) {
        em.remove(em.contains(beacon) ? beacon : em.merge(beacon));
    }

    public void update(Beacon beacon) {
        em.merge(beacon);
    }
}
