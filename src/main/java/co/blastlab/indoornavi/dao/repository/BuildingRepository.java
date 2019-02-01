package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Building;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends EntityRepository<Building, Long> {

	Optional<Building> findOptionalById(Long id);
}
