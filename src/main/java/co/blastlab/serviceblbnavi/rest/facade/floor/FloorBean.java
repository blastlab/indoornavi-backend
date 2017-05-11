package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Scale;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	@Override
	public FloorDto get(Long id) {
		Optional<Floor> floorEntity = floorRepository.findById(id);
		if (floorEntity.isPresent()) {
			return new FloorDto(floorEntity.get());
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto create(FloorDto floor) {
		Optional<Building> building = buildingRepository.findById(floor.getBuildingId());
		if (building.isPresent()) {
			Floor floorEntity = new Floor();
			floorEntity.setLevel(floor.getLevel());
			floorEntity.setName(floor.getName());
			floorEntity.setBuilding(building.get());
			floorEntity = floorRepository.save(floorEntity);
			return new FloorDto(floorEntity);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto update(Long id, FloorDto floor) {
		Optional<Building> building = buildingRepository.findById(floor.getBuildingId());
		if(building.isPresent()){
			Optional<Floor> floorEntity = floorRepository.findById(id);
			if (floorEntity.isPresent()) {
				floorEntity.get().setLevel(floor.getLevel());
				floorEntity.get().setName(floor.getName());
				Floor floorDb = floorRepository.save(floorEntity.get());
				return new FloorDto(floorDb);
			}
		}
		throw new EntityNotFoundException();
	}

	@Override
	public List<FloorDto> updateLevels(List<FloorDto> floors) throws Exception {
		List<FloorDto> updatedFloors = new ArrayList<>();
		Map<Floor, Integer> floorEntityToLevel = new HashMap<>();
		// We need to set null to all levels before we set new values due to unique constraint
		floors.forEach((floorDto) -> {
			Optional<Floor> floorOptional = floorRepository.findById(floorDto.getId());
			if (floorOptional.isPresent()) {
				Floor floorEntity = floorOptional.get();
				floorEntity.setLevel(null);
				floorRepository.saveAndFlush(floorEntity);
				floorEntityToLevel.put(floorEntity, floorDto.getLevel());
			} else {
				throw new EntityNotFoundException();
			}
		});
		floorEntityToLevel.keySet().forEach((floor -> {
			floor.setLevel(floorEntityToLevel.get(floor));
			updatedFloors.add(new FloorDto(floorRepository.save(floor)));
		}));
		return updatedFloors;
	}

	@Override
	public Response delete(Long id) {
		Optional<Floor> floor = floorRepository.findById(id);
		if (floor.isPresent()) {
			floorRepository.remove(floor.get());
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response setScale(Long id, ScaleDto scaleDto) {
		Optional<Floor> floorOptional = floorRepository.findById(id);
		if (floorOptional.isPresent()) {
			Floor floor = floorOptional.get();
			Scale scale = new Scale();
			scale.setScale(scaleDto.getScale());
			scale.setMeasure(scaleDto.getMeasure());
			scale.setStartX(scaleDto.getStart().getX());
			scale.setStartY(scaleDto.getStart().getY());
			scale.setStopX(scaleDto.getStop().getX());
			scale.setStopY(scaleDto.getStop().getY());
			floor.setScale(scale);
			floorRepository.save(floor);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}
}