package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import io.swagger.annotations.ApiParam;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@Stateless
public class AnchorBean implements AnchorFacade {

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	public AnchorDto create(AnchorDto anchor) {
		Anchor anchorEntity = new Anchor();
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());
		anchorEntity.setLongId(anchor.getLongId());
		anchorEntity.setShortId(anchor.getShortId());
		anchorRepository.save(anchorEntity);
		return new AnchorDto(anchorEntity);
	}

	public AnchorDto setFloor(AnchorDto anchor) {
		Anchor anchorEntity = anchorRepository.findBy(anchor.getId());
		if (anchorEntity != null){
			Floor floor = floorRepository.findBy(anchor.getFloorId());
			if (floor != null){
				anchorEntity.setFloor(floor);
				anchorRepository.save(anchorEntity);
				return new AnchorDto(anchorEntity);
			}
		}
		throw new EntityExistsException();
	}

	public AnchorDto setShortId( String longId, String shortId) {
		Anchor anchorEntity = anchorRepository.findOptionalByLongId(longId);
		if (anchorEntity != null){
			anchorEntity.setShortId(shortId);
			anchorRepository.save(anchorEntity);
			return new AnchorDto(anchorEntity);
		}
		throw new EntityNotFoundException();
	}

	public AnchorDto findByLongId(String longId) {
		Anchor anchorEntity = anchorRepository.findOptionalByLongId(longId);
		if (anchorEntity != null){
			return new AnchorDto(anchorEntity);
		}
		throw new EntityNotFoundException();
	}
}
