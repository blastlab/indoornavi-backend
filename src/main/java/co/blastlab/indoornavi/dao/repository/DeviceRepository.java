package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Device;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface DeviceRepository extends EntityRepository<Device, Long> {
}
