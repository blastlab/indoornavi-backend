package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Building_;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Complex_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import java.util.List;

@Repository
public abstract class ComplexRepository implements EntityRepository<Complex, Long>, CriteriaSupport<Complex> {

    public abstract Complex findOptionalByName(String name);

    public Complex findByBuildingId(Long id) {
        return criteria()
                .join(Complex_.buildings, where(Building.class).eq(Building_.id, id))
                .getSingleResult();
    }

    @Query("SELECT c FROM Complex c JOIN c.ACL_complexes aclComplexes where aclComplexes.person.id = ?1")
    public abstract List<Complex> findAllByPerson(Long personId);  //TODO Change @Query to criteria api

    @Query("SELECT c FROM Complex c JOIN c.buildings buildings JOIN buildings.floors floors where floors.id = ?1")
    public abstract Complex findByFloorId(Long id); //TODO Change @Query to criteria api
}