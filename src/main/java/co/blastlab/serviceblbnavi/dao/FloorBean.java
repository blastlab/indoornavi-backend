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
    FloorRepository floorRepository;

    // TODO: we need this for upload image: FileUploadServlet tries to save floor via floorRepository which has no transaction
    public Floor save(Floor floor) {
        return floorRepository.save(floor);
    }
}
