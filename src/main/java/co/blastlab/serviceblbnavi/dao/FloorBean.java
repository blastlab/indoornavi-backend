package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Floor;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class FloorBean {

    @Inject
    private EntityManager em;

    @Inject
    FloorRepository floorRepository;

    public Floor find(Long id) {
        return em.find(Floor.class, id);
    }

    public void updateFloorLevels(List<Floor> floors) {
        floors.stream().map((f) -> {
            Floor floor = floorRepository.findBy(f.getId());
            floor.setLevel(f.getLevel());
            return floor;
        }).forEach((floor) -> {
            floorRepository.save(floor);
        });
    }

    // TODO: we need this for upload image: FileUploadServlet tries to save floor via floorRepository which has no transaction
    public Floor save(Floor floor) {
        return floorRepository.save(floor);
    }
}
