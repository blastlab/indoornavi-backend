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

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	public FloorDto.WithId create(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = new Floor();
			floorEntity.setLevel(floor.getLevel());
			floorEntity.setName(floor.getName());
			floorEntity.setBuilding(building);
			floorEntity = floorRepository.save(floorEntity);
			return new FloorDto.WithId(floorEntity);
		}
		throw new EntityNotFoundException();
	}

	public FloorDto.WithId update(Long id, FloorDto floor) {
		Building building = buildingRepository.findBy(id);
		if(building != null){
			Floor floorEntity = floorRepository.findBy(id);
			floorEntity.setLevel(floor.getLevel());
			floorEntity.setName(floor.getName());
			floorEntity = floorRepository.save(floorEntity);
			return new FloorDto.WithId(floorEntity);
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
}