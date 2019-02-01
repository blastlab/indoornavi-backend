package co.blastlab.indoornavi.rest.facade.building;

import co.blastlab.indoornavi.dao.repository.BuildingRepository;
import co.blastlab.indoornavi.dao.repository.ComplexRepository;
import co.blastlab.indoornavi.domain.Building;
import co.blastlab.indoornavi.domain.Complex;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.building.BuildingDto;
import co.blastlab.indoornavi.service.FloorService;
import co.blastlab.indoornavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Stateless
public class BuildingBean implements BuildingFacade {

	@Inject
	private Logger logger;

	@Inject
	private BuildingRepository buildingRepository;

	@Inject
	private ComplexRepository complexRepository;

	@Inject
	private FloorService floorService;

	@Override
	public BuildingDto create(BuildingDto building) {
		logger.debug("Trying to create building {}", building);
		Optional<Complex> complex = complexRepository.findById(building.getComplex().getId());
		if (complex.isPresent()) {
			Building buildingEntity = new Building();
			buildingEntity.setComplex(complex.get());
			buildingEntity.setName(building.getName());
			buildingEntity = buildingRepository.save(buildingEntity);
			logger.debug("Building created");
			return new BuildingDto(buildingEntity);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public BuildingDto update(Long id, BuildingDto building) {
		logger.debug("Trying to update building {}", building);
		Optional<Complex> complex = complexRepository.findByBuildingId(id);
		if (complex.isPresent()) {
			logger.debug("Complex found");
			Optional<Building> buildingEntity = buildingRepository.findOptionalById(id);
			if (buildingEntity.isPresent()) {
				buildingEntity.get().setComplex(complex.get());
				buildingEntity.get().setName(building.getName());
				Building buildingDb = buildingRepository.save(buildingEntity.get());
				logger.debug("Building updated");
				return new BuildingDto(buildingDb);
			}
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove building id = {}", id);
		Optional<Building> building = buildingRepository.findOptionalById(id);
		if (building.isPresent()) {
			for (Floor floor : building.get().getFloors()) {
				floorService.removeNoCommit(floor);
			}
			buildingRepository.remove(building.get());
			logger.debug("Building removed");
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public BuildingDto.WithFloors find(Long id) {
		Optional<Building> buildingEntity = buildingRepository.findOptionalById(id);
		if (buildingEntity.isPresent()) {
			return new BuildingDto.WithFloors(buildingEntity.get());
		}
		throw new EntityNotFoundException();
	}
}
