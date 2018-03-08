package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Device;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends EntityRepository<Device, Long> {
	Optional<Device> findOptionalByShortId(Integer shortId);
}
