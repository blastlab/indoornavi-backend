package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.ApiKey;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends EntityRepository<ApiKey, Long>{
	Optional<ApiKey> findOptionalByValue(String value);
}
