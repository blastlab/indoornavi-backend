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
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AnchorBean implements AnchorFacade {

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	public AnchorDto.WithFloor.WithId create(AnchorDto anchor) {
		Anchor anchorEntity = new Anchor();
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());
		anchorEntity.setLongId(anchor.getLongId());
		anchorEntity.setShortId(anchor.getShortId());
		anchorRepository.save(anchorEntity);
		return new AnchorDto.WithFloor.WithId(anchorEntity);
	}

	public AnchorDto.WithFloor.WithId setFloor(Long id, AnchorDto.WithFloor anchor) {
		Anchor anchorEntity = anchorRepository.findBy(id);
		if (anchorEntity != null) {
			Floor floor = floorRepository.findBy(anchor.getFloorId());
			if (floor != null) {
				anchorEntity.setFloor(floor);
				anchorRepository.save(anchorEntity);
				return new AnchorDto.WithFloor.WithId(anchorEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public AnchorDto.WithFloor.WithId find(Long id) {
		Anchor anchorEntity = anchorRepository.findBy(id);
		if (anchorEntity != null) {
			return new AnchorDto.WithFloor.WithId(anchorEntity);
		}
		throw new EntityNotFoundException();
	}

	public List<AnchorDto.WithFloor.WithId> findByFloor(Long floorId) {
		if (floorId != null){
			Floor floor = floorRepository.findBy(floorId);
			if (floor != null){
				List<AnchorDto.WithFloor.WithId> anchors = new ArrayList<>();
				anchorRepository.findByFloor(floor).forEach((anchorEntity -> anchors.add(new AnchorDto.WithFloor.WithId(anchorEntity))));
				return anchors;
			}
		}
		throw new EntityNotFoundException();
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
