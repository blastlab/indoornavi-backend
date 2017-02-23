package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends EntityRepository<Goal, Long> {

    List<Goal> findByFloor(Floor floor);

    List<Goal> findByFloorAndInactive(Floor floor, Boolean inactive);
}
