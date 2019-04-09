package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.AreaConfiguration;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface AreaConfigurationRepository extends EntityRepository<AreaConfiguration, Long> {
	Optional<AreaConfiguration> findOptionalById(Long id);
}
