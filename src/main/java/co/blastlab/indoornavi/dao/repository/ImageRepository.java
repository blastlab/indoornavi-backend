package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Image;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends EntityRepository<Image, Long> {

	Optional<Image> findOptionalById(Long id);

	Optional<Image> findOptionalByFloor(Floor floor);
}
