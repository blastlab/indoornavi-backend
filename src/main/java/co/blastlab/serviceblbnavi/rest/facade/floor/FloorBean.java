package co.blastlab.serviceblbnavi.rest.facade.floor;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import org.apache.http.HttpStatus;

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

	@Override
	public FloorDto create(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = new Floor();
			floorEntity.setLevel(floor.getLevel());
			floorEntity.setName(floor.getName());
			floorEntity.setBuilding(building);
			floorEntity = floorRepository.save(floorEntity);
			return new FloorDto(floorEntity);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public FloorDto update(Long id, FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if(building != null){
			Floor floorEntity = floorRepository.findBy(id);
			if (floorEntity != null) {
				floorEntity.setLevel(floor.getLevel());
				floorEntity.setName(floor.getName());
				floorEntity = floorRepository.save(floorEntity);
				return new FloorDto(floorEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response delete(Long id) {
		Floor floor = floorRepository.findBy(id);
		if (floor != null) {
			floorRepository.remove(floor);
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}
}