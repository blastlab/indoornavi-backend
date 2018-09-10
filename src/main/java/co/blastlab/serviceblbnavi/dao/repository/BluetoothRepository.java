package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Bluetooth;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface BluetoothRepository extends EntityRepository<Bluetooth, Long> {
	Optional<Bluetooth> findOptionalById(Long id);
}
