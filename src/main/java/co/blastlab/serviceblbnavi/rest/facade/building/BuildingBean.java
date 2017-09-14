package co.blastlab.serviceblbnavi.rest.facade.building;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Stateless
public class BuildingBean implements BuildingFacade {

	@Inject
	private BuildingRepository buildingRepository;

	@Inject
	private ComplexRepository complexRepository;

	@Override
	public BuildingDto create(BuildingDto building) {
		Optional<Complex> complex = complexRepository.findById(building.getComplexId());
		if (complex.isPresent()) {
			Building buildingEntity = new Building();
			buildingEntity.setComplex(complex.get());
			buildingEntity.setName(building.getName());
			buildingEntity = buildingRepository.save(buildingEntity);
			return new BuildingDto(buildingEntity);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public BuildingDto update(Long id, BuildingDto building) {
		Optional<Complex> complex = complexRepository.findByBuildingId(id);
		if (complex.isPresent()) {
			Optional<Building> buildingEntity = buildingRepository.findOptionalById(id);
			if (buildingEntity.isPresent()) {
				buildingEntity.get().setComplex(complex.get());
				buildingEntity.get().setName(building.getName());
				Building buildingDb = buildingRepository.save(buildingEntity.get());
				return new BuildingDto(buildingDb);
			}
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response delete(Long id) {
		Optional<Building> building = buildingRepository.findOptionalById(id);
		if (building.isPresent()) {
			buildingRepository.remove(building.get());
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