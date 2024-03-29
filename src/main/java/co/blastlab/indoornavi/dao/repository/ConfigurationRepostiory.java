package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Configuration;
import co.blastlab.indoornavi.domain.Floor;
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
	Optional<Configuration> findTop1ByFloorAndPublishedDateIsNotNullOrderByVersionDesc(Floor floor);
	List<Configuration> findByFloor(Floor floor);
}
