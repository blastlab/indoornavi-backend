package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Floor_;
import co.blastlab.serviceblbnavi.domain.Publication;
import co.blastlab.serviceblbnavi.domain.Publication_;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class PublicationRepository implements EntityRepository<Publication, Long>, CriteriaSupport<Publication> {
	public abstract Optional<Publication> findOptionalById(Long id);

	public List<Publication> findAllContainingFloor(Floor floor) {
		return criteria()
			.join(Publication_.floors,
				where(
					Floor.class
				).eq(
					Floor_.id, floor.getId()
				)
			)
			.getResultList();
	}
}
