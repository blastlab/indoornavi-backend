package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Building_;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Complex_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import java.util.Optional;

@Repository
public abstract class ComplexRepository implements EntityRepository<Complex, Long>, CriteriaSupport<Complex> {

	public abstract Complex findOptionalByName(String name);

	public Optional<Complex> findByBuildingId(Long id) {
		return Optional.ofNullable(
			criteria()
				.join(Complex_.buildings, where(Building.class).eq(Building_.id, id))
				.getOptionalResult()
		);
	}
}