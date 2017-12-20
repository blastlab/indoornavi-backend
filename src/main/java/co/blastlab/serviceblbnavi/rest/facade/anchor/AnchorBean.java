package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.rest.facade.device.DeviceBean;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class AnchorBean extends DeviceBean implements AnchorFacade {

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	public AnchorDto create(AnchorDto anchor) {
		Anchor anchorEntity = new Anchor();
		anchorEntity.setShortId(anchor.getShortId());
		anchorEntity.setLongId(anchor.getLongId());
		anchorEntity.setName(anchor.getName());
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());

		if (anchor.getFloorId() != null) {
			super.setFloor(anchor, anchorEntity);
		}
		anchorRepository.save(anchorEntity);
		return new AnchorDto(anchorEntity);
	}

	public AnchorDto update(Long id, AnchorDto anchor) {
		Optional<Anchor> anchorOptional = anchorRepository.findById(id);
		if (anchorOptional.isPresent()) {
			Anchor anchorEntity = anchorOptional.get();
			anchorEntity.setX(anchor.getX());
			anchorEntity.setY(anchor.getY());
			anchorEntity.setName(anchor.getName());
			anchorEntity.setVerified(anchor.getVerified());
			anchorEntity.setLongId(anchor.getLongId());
			anchorEntity.setShortId(anchor.getShortId());

			if (anchor.getFloorId() != null) {
				super.setFloor(anchor, anchorEntity);
			} else {
				anchorEntity.setFloor(null);
			}
			anchorRepository.save(anchorEntity);
			return new AnchorDto(anchorEntity);
		}
		throw new EntityNotFoundException();
	}

	public List<AnchorDto> findAll() {
		List<AnchorDto> anchors = new ArrayList<>();
		anchorRepository.findAll()
			.forEach(anchorEntity -> {
				if(!(anchorEntity instanceof Sink)){
					anchors.add(new AnchorDto(anchorEntity));
				}
			});
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
}
