package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.PublicationRepository;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

import static co.blastlab.serviceblbnavi.domain.Scale.scale;

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Inject
	private PublicationRepository publicationRepository;

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
		Building building = buildingRepository.findOptionalById(floor.getBuilding().getId()).orElseThrow(EntityNotFoundException::new);
		Floor floorEntity = new Floor();
		floorEntity.setLevel(floor.getLevel());
		floorEntity.setName(floor.getName());
		floorEntity.setBuilding(building);
		floorEntity = floorRepository.save(floorEntity);
		return new FloorDto(floorEntity);
	}

	@Override
	public FloorDto update(Long id, FloorDto floor) {
		Floor floorEntity = floorRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		floorEntity.setLevel(floor.getLevel());
		floorEntity.setName(floor.getName());
		Floor floorDb = floorRepository.save(floorEntity);
		return new FloorDto(floorDb);
	}

	@Override
	public List<FloorDto> updateLevels(List<FloorDto> floors) throws Exception {
		List<FloorDto> updatedFloors = new ArrayList<>();
		Map<Floor, Integer> floorEntityToLevel = new HashMap<>();
		// We need to set null to all levels before we set new values due to unique constraint
		floors.forEach((floorDto) -> {
			Optional<Floor> floorOptional = floorRepository.findOptionalById(floorDto.getId());
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
		Optional<Floor> floorOptional = floorRepository.findOptionalById(id);
		if (floorOptional.isPresent()) {
			Floor floor = floorOptional.get();
			List<Configuration> byFloor = configurationRepostiory.findByFloor(floor);
			for (Configuration configuration : byFloor) {
				configurationRepostiory.remove(configuration);
			}
			List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
			for (Publication publication : publications) {
				// if this is the last floor in this publication, we have to remove it
				if (publication.getFloors().size() == 1) {
					publicationRepository.remove(publication);
				}
			}
			floorRepository.remove(floor);
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto setScale(Long id, ScaleDto scaleDto) {
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
		return new FloorDto(floor);
	}
}