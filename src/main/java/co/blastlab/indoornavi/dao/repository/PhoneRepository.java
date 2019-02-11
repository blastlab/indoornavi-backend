package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Phone;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends EntityRepository<Phone, Long> {
	Optional<Phone> findOptionalById(Long id);
	Optional<Phone> findOptionalByUserData(String userData);
}
