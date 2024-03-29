package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.User;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends EntityRepository<User, Long> {
	Optional<User> findOptionalByUsername(String username);
	Optional<User> findOptionalByToken(String token);
	Optional<User> findOptionalById(Long id);
}
