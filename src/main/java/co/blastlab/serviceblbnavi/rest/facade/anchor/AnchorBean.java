package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

@Stateless
public class AnchorBean implements AnchorFacade {

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	public AnchorDto create(AnchorDto anchor) {
		Anchor anchorEntity = new Anchor();
		anchorEntity.setName(anchor.getName());
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());
		anchorEntity.setShortId(anchor.getShortId());
		anchorEntity.setLongId(anchor.getLongId());

		if (anchor.getFloorId() != null) {
			setFloor(anchor, anchorEntity);
		}
		anchorRepository.save(anchorEntity);
		return new AnchorDto(anchorEntity);
	}

	public AnchorDto update(Long id, AnchorDto anchor) {
		Optional<Anchor> anchorEntity = anchorRepository.findById(id);
		if (anchorEntity.isPresent()) {
			anchorEntity.get().setX(anchor.getX());
			anchorEntity.get().setY(anchor.getY());
			anchorEntity.get().setName(anchor.getName());

			if (anchor.getFloorId() != null) {
				setFloor(anchor, anchorEntity.get());
			} else {
				anchorEntity.get().setFloor(null);
			}
			anchorRepository.save(anchorEntity.get());
			return new AnchorDto(anchorEntity.get());
		}
		throw new EntityNotFoundException();
	}

	public List<AnchorDto> findAll() {
		List<AnchorDto> anchors = new ArrayList<>();
		anchorRepository.findAll()
			.forEach(anchorEntity -> anchors.add(new AnchorDto(anchorEntity)));
		return anchors;
	}

	public Response delete(Long id) {
		Optional<Anchor> anchor = anchorRepository.findById(id);
		if (anchor.isPresent()) {
			anchorRepository.remove(anchor.get());
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}

	private void setFloor(AnchorDto anchor, Anchor anchorEntity) {
		Optional<Floor> floor = floorRepository.findById(anchor.getFloorId());
		if (floor.isPresent()) {
			anchorEntity.setFloor(floor.get());
		} else {
			throw new EntityNotFoundException();
		}
	}
}
