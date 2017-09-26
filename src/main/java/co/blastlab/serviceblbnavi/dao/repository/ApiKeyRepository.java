package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.ApiKey;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends EntityRepository<ApiKey, Long>{
	Optional<ApiKey> findOptionalByValue(String value);
}
