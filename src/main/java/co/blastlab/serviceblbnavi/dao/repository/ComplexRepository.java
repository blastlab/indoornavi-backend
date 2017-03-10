package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Building_;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Complex_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public abstract class ComplexRepository implements EntityRepository<Complex, Long>, CriteriaSupport<Complex> {

	public abstract Complex findOptionalByName(String name);

	public Complex findByBuildingId(Long id) {
		try{
			return criteria()
				.join(Complex_.buildings, where(Building.class).eq(Building_.id, id))
				.getSingleResult();
		}
		catch (NoResultException e){
			return null;
		}
	}
}