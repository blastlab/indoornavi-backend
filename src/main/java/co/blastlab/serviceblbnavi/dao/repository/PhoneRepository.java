package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Phone;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends EntityRepository<Phone, Long> {
	Optional<Phone> findOptionalById(Long id);
	Optional<Phone> findOptionalByUserData(String userData);
}
