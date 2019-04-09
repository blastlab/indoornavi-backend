package co.blastlab.indoornavi.rest.facade.anchor;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.service.AnchorService;
import co.blastlab.indoornavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class AnchorBean implements AnchorFacade {

	@Inject
	private Logger logger;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private AnchorService anchorService;

	public AnchorDto create(AnchorDto anchor) {
		logger.debug("Trying to create anchor {}", anchor);
		Anchor anchorEntity = new Anchor();
		anchorEntity.setShortId(anchor.getShortId());
		anchorEntity.setMac(anchor.getMacAddress());
		anchorEntity.setName(anchor.getName());
		anchorEntity.setX(anchor.getX());
		anchorEntity.setY(anchor.getY());

		if (anchor.getFloor() != null) {
			anchorService.setFloor(anchor, anchorEntity);
		}
		anchorRepository.save(anchorEntity);
		logger.debug("Anchor created");
		return new AnchorDto(anchorEntity);
	}

	public AnchorDto update(Long id, AnchorDto anchor) {
		logger.debug("Trying to update anchor {}", anchor);
		Optional<Anchor> anchorOptional = anchorRepository.findById(id);
		if (anchorOptional.isPresent()) {
			Anchor anchorEntity = anchorOptional.get();
			anchorEntity.setX(anchor.getX());
			anchorEntity.setY(anchor.getY());
			anchorEntity.setName(anchor.getName());
			anchorEntity.setVerified(anchor.getVerified());
			anchorEntity.setMac(anchor.getMacAddress());
			anchorEntity.setShortId(anchor.getShortId());

			if (anchor.getFloor() != null) {
				anchorService.setFloor(anchor, anchorEntity);
			} else {
				anchorEntity.setFloor(null);
			}
			anchorRepository.save(anchorEntity);
			logger.debug("Anchor updated");
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
		logger.debug("Trying to remove anchor id = {}", id);
		Optional<Anchor> anchor = anchorRepository.findById(id);
		if (anchor.isPresent()) {
			anchorRepository.remove(anchor.get());
			logger.debug("Anchor removed");
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}
}
