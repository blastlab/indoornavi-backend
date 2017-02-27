package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends EntityRepository<Building, Long> {

	List<Building> findByComplex(Complex complex);

	Building findOptionalByComplexAndName(Complex complex, String buildingName);
}
