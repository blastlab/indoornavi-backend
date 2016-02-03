package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import java.util.List;
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

    public List<Beacon> findAll(Floor floor) {
        return em.createNamedQuery(Beacon.FIND_BY_FLOOR).setParameter("floor", floor).getResultList();
    }

    public void insertSQL(Beacon beacon) {
        em.createNativeQuery("INSERT INTO Beacon (id, mac, x, y, z, floor_id, minor, major) VALUES (:id, :mac, :x, :y, :z, :floor_id, :minor, :major)")
                .setParameter("id", beacon.getId())
                .setParameter("mac", beacon.getMac())
                .setParameter("x", beacon.getX())
                .setParameter("y", beacon.getY())
                .setParameter("z", beacon.getZ())
                .setParameter("floor_id", beacon.getFloor().getId())
                .setParameter("minor", beacon.getMinor())
                .setParameter("major", beacon.getMajor())
                .executeUpdate();
    }
}
