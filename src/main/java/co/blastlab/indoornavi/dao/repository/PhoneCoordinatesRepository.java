package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.PhoneCoordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface PhoneCoordinatesRepository extends EntityRepository<PhoneCoordinates, Long> {
}
