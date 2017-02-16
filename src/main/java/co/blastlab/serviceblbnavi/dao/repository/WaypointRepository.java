package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface WaypointRepository extends EntityRepository<Waypoint, Long> {

    List<Waypoint> findByFloorAndInactive(Floor floor, Boolean inactive);

    List<Waypoint> findByFloor(Floor floor);
}
