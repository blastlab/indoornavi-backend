package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Bluetooth;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface BluetoothRepository extends EntityRepository<Bluetooth, Long> {
	Optional<Bluetooth> findOptionalById(Long id);
}
