package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface BeaconRepository extends EntityRepository<Beacon, Long> {

	List<Beacon> findByFloor(Floor floor);
}