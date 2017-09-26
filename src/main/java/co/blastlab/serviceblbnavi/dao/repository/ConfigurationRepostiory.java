package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepostiory extends EntityRepository<Configuration, Long> {
	@Query("SELECT MAX(c.version) FROM Configuration c WHERE c.floor = ?1")
	Integer getLatestVersion(Floor floor);

	List<Configuration> findByFloorOrderByVersionDesc(Floor floor);
	Optional<Configuration> findTop1ByFloorOrderByVersionDesc(Floor floor);
	Optional<Configuration> findTop1ByFloorAndPublishedOrderByVersionDesc(Floor floor, Boolean published);
	List<Configuration> findByFloor(Floor floor);
}
