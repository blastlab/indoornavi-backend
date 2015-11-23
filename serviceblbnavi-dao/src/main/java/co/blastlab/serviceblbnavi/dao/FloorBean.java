package co.blastlab.serviceblbnavi.dao;

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
public class FloorBean {

    @Inject
    private EntityManager em;

    public void create(Floor floor) {
        em.persist(floor);
    }

    public Floor find(Long id) {
        return em.find(Floor.class, id);
    }

    public void update(Floor floor) {
        em.merge(floor);
    }

    public void delete(Floor floor) {
        em.remove(em.contains(floor) ? floor : em.merge(floor));
    }

    public void updateFloorLevels(List<Floor> floors) {
        floors.stream().map((f) -> {
            Floor floor = find(f.getId());
            floor.setLevel(f.getLevel());
            return floor;
        }).forEach((floor) -> {
            update(floor);
        });
    }
}
