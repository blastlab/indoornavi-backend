package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface BuildingConfigurationRepository extends EntityRepository<BuildingConfiguration, Long> {

    @Query("SELECT bc FROM BuildingConfiguration bc WHERE bc.building.name = ?2 AND bc.building.complex.name = ?1 AND bc.version = ?3")
    BuildingConfiguration findByComplexNameAndBuildingNameAndVersion(String complexName, String buildingName, int version);

    BuildingConfiguration findOptionalByBuildingAndVersion(Building building, Integer version);
}
