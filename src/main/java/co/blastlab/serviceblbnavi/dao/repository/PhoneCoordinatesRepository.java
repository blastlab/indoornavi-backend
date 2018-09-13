package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.PhoneCoordinates;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface PhoneCoordinatesRepository extends EntityRepository<PhoneCoordinates, Long> {
}
