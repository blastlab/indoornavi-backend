package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Coordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface CoordinatesRepository extends EntityRepository<Coordinates, Long> {
	Coordinates findByDevice(String device);
}
