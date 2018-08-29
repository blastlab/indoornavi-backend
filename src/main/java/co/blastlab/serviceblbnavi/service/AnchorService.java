package co.blastlab.serviceblbnavi.service;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.utils.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public class AnchorService {
	@Inject
	private Logger logger;

	@Inject
	private FloorRepository floorRepository;

	public void setFloor(AnchorDto anchorDto, Anchor anchor) {
		logger.debug("Trying to assign floor to device {}", anchorDto);
		Optional<Floor> floor = floorRepository.findOptionalById(anchorDto.getFloorId());
		if (floor.isPresent()) {
			anchor.setFloor(floor.get());
			logger.debug("Floor assigned");
		} else {
			throw new EntityNotFoundException();
		}
	}
}
