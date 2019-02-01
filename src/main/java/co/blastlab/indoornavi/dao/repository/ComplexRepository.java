package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Building;
import co.blastlab.indoornavi.domain.Building_;
import co.blastlab.indoornavi.domain.Complex;
import co.blastlab.indoornavi.domain.Complex_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import java.util.Optional;

@Repository
public abstract class ComplexRepository implements EntityRepository<Complex, Long>, CriteriaSupport<Complex> {

	public abstract Optional<Complex> findById(Long id);

	public Optional<Complex> findByBuildingId(Long id) {
		return Optional.ofNullable(
			criteria()
				.join(Complex_.buildings, where(Building.class).eq(Building_.id, id))
				.getOptionalResult()
		);
	}
}
