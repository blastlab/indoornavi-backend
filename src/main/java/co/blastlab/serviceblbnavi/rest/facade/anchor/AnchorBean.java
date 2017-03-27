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
			Floor floor = floorRepository.findBy(anchor.getFloorId());
			if (floor != null) {
				anchorEntity.setFloor(floor);
			} else {
				throw new EntityNotFoundException();
			}
		}

		anchorRepository.save(anchorEntity);
		return new AnchorDto(anchorEntity);
	}

	public AnchorDto update(Long id, AnchorDto anchor) {
		Anchor anchorEntity = anchorRepository.findBy(id);
		if (anchorEntity != null) {
			anchorEntity.setX(anchor.getX());
			anchorEntity.setY(anchor.getY());

			if (anchor.getName() != null) {
				anchorEntity.setName(anchor.getName());
			}

			if (anchor.getFloorId() != null) {
				Floor floor = floorRepository.findBy(anchor.getFloorId());
				if (floor != null) {
					anchorEntity.setFloor(floor);
				}
			}
			anchorRepository.save(anchorEntity);
			return new AnchorDto(anchorEntity);
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
		if (id != null) {
			Anchor anchor = anchorRepository.findBy(id);
			if (anchor != null) {
				anchorRepository.remove(anchor);
				return Response.status(HttpStatus.SC_NO_CONTENT).build();
			}
		}
		throw new EntityNotFoundException();
	}
}
