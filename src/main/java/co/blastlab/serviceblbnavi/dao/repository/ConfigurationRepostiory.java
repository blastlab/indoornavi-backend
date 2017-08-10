package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface ConfigurationRepostiory extends EntityRepository<Configuration, Long> {
	@Query("SELECT MAX(c.version) FROM Configuration c WHERE c.floor = ?1")
	Integer getLatestVersion(Floor floor);

	List<Configuration> findByFloorOrderByVersionDesc(Floor floor);
}
