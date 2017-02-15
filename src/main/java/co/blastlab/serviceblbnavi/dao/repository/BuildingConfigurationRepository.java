package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface BuildingConfigurationRepository extends EntityRepository<BuildingConfiguration, Long> {

    BuildingConfiguration findOptionalByBuildingAndVersion(Building building, Integer version);
}
