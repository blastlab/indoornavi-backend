package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Scale;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.service.FloorService;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static co.blastlab.serviceblbnavi.domain.Scale.scale;

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private Logger logger;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	@Inject
	private FloorService floorService;

	@Override
	public FloorDto get(Long id) {
		Optional<Floor> floorEntity = floorRepository.findOptionalById(id);
		if (floorEntity.isPresent()) {
			return new FloorDto(floorEntity.get());
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto create(FloorDto floor) {
		logger.debug("Trying to create floor {}", floor);
		Building building = buildingRepository.findOptionalById(floor.getBuilding().getId()).orElseThrow(EntityNotFoundException::new);
		Floor floorEntity = new Floor();
		floorEntity.setLevel(floor.getLevel());
		floorEntity.setName(floor.getName());
		floorEntity.setBuilding(building);
		floorEntity = floorRepository.save(floorEntity);
		logger.debug("Floor created");
		return new FloorDto(floorEntity);
	}

	@Override
	public FloorDto update(Long id, FloorDto floor) {
		logger.debug("Trying to update floor {}", floor);
		Floor floorEntity = floorRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		floorEntity.setLevel(floor.getLevel());
		floorEntity.setName(floor.getName());
		Floor floorDb = floorRepository.save(floorEntity);
		logger.debug("Floor updated");
		return new FloorDto(floorDb);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove floor id = {}", id);
		Optional<Floor> floorOptional = floorRepository.findOptionalById(id);
		if (floorOptional.isPresent()) {
			Floor floor = floorOptional.get();
			floorService.remove(floor);
			logger.debug("Floor removed");
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto setScale(Long id, ScaleDto scaleDto) {
		logger.debug("Trying to set scale {} to floor id {}", scaleDto, id);
		Floor floor = floorRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		Scale scale = scale(floor.getScale())
			.measure(scaleDto.getMeasure())
			.distance(scaleDto.getRealDistance())
			.startX(scaleDto.getStart().getX())
			.startY(scaleDto.getStart().getY())
			.stopX(scaleDto.getStop().getX())
			.stopY(scaleDto.getStop().getY());
		floor.setScale(scale);
		floor = floorRepository.save(floor);
		logger.debug("Scale set");
		return new FloorDto(floor);
	}
}