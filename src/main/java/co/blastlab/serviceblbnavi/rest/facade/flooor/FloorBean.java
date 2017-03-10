package co.blastlab.serviceblbnavi.rest.facade.flooor;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	public FloorDto create(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = new Floor();
			return createOrUpdate(floorEntity, floor, building);
		}
		throw new EntityNotFoundException();
	}

	public FloorDto find(Long id) {
		Floor floor = floorRepository.findBy(id);
		if (floor != null) {
			return new FloorDto(floor);
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		Floor floor = floorRepository.findBy(id);
		if (floor != null) {
			floorRepository.remove(floor);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public FloorDto update(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = floorRepository.findBy(floor.getId());
			return createOrUpdate(floorEntity, floor, building);
		}
		throw new EntityNotFoundException();
	}

	public Response updateFloors(Long buildingId, List<FloorDto> floors) {
		Building building = buildingRepository.findBy(buildingId);
		if (building != null) {
			List<Floor> floorEntities = new ArrayList<>();
			floors.forEach((floor -> {
				Floor floorEntity = floorRepository.findBy(floor.getId());
				if (floorEntity == null) {
					throw new EntityNotFoundException();
				}
				floorEntities.add(floorEntity);
			}
			));

			floorEntities.forEach((floorEntity -> floorEntity.setBuilding(building)));
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	private FloorDto createOrUpdate(Floor floorEntity, FloorDto floor, Building building) {
		floorEntity.setBuilding(building);
		floorEntity = floorRepository.save(floorEntity);
		return new FloorDto(floorEntity);
	}
}