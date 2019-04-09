package co.blastlab.indoornavi.service;

import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

public class AnchorService {
	@Inject
	private Logger logger;

	@Inject
	private FloorRepository floorRepository;

	public void setFloor(AnchorDto anchorDto, Anchor anchor) {
		logger.debug("Trying to assign floor to device {}", anchorDto);
		Floor floor = floorRepository.findOptionalById(anchorDto.getFloor().getId()).orElseThrow(EntityNotFoundException::new);
		anchor.setFloor(floor);
		logger.debug("Floor assigned");
	}
}
