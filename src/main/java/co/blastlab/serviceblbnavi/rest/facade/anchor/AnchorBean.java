package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;

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

	public AnchorDto.WithId create(AnchorDto anchor) {
		Anchor anchorEntity = new Anchor();
		anchorEntity.setName(anchor.getName());
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());
		anchorEntity.setLongId(anchor.getLongId());
		anchorEntity.setShortId(anchor.getShortId());

		if (anchor.getFloorId() != null) {
			Floor floor = floorRepository.findBy(anchor.getFloorId());
			if (floor != null) {
				anchorEntity.setFloor(floor);
			} else {
				throw new EntityNotFoundException();
			}
		}

		anchorRepository.save(anchorEntity);
		return new AnchorDto.WithId(anchorEntity);
	}

	public AnchorDto.WithId update(Long id, AnchorDto anchor) {
		Anchor anchorEntity = anchorRepository.findBy(id);
		if (anchorEntity != null) {
			Floor floor = floorRepository.findBy(anchor.getFloorId());
			if (floor != null) {
				anchorEntity.setName(anchor.getName());
				anchorEntity.setX(anchor.getX());
				anchorEntity.setY(anchor.getY());
				anchorEntity.setFloor(floor);
				anchorRepository.save(anchorEntity);
				return new AnchorDto.WithId(anchorEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public AnchorDto.WithId find(Long id) {
		Anchor anchorEntity = anchorRepository.findBy(id);
		if (anchorEntity != null) {
			return new AnchorDto.WithId(anchorEntity);
		}
		throw new EntityNotFoundException();
	}

	public List<AnchorDto.WithId> findAll() {
		List<AnchorDto.WithId> anchors = new ArrayList<>();
		anchorRepository.findAll()
			.forEach(anchorEntity -> anchors.add(new AnchorDto.WithId(anchorEntity)));
		return anchors;
	}

	public Response delete(Long id) {
		if (id != null) {
			Anchor anchor = anchorRepository.findBy(id);
			if (anchor != null) {
				anchorRepository.remove(anchor);
				return Response.ok().build();
			}
		}
		throw new EntityNotFoundException();
	}
}
