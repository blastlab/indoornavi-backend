package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.ProductionDBEntityManagerResolver;
import co.blastlab.serviceblbnavi.domain.Building;
import org.apache.deltaspike.data.api.EntityManagerConfig;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import javax.persistence.FlushModeType;

@Repository
@EntityManagerConfig(entityManagerResolver = ProductionDBEntityManagerResolver.class, flushMode = FlushModeType.COMMIT)
public interface BuildingProductionRepository extends EntityRepository<Building, Long> {
}
