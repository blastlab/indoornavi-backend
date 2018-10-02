package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Image;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends EntityRepository<Image, Long> {

	Optional<Image> findOptionalById(Long id);
}
