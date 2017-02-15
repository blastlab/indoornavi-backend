package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends EntityRepository<Building, Long>{

    List<Building> findByComplex(Complex complex);

    //@Query("SELECT b FROM Building b WHERE b.complex.name = ?1 AND b.name = ?2")
    Building findOptionalByComplexAndName(Complex complex, String buildingName);
}
