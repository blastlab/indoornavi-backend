package co.blastlab.serviceblbnavi.rest.facade.building;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;

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

	public BuildingDto create(BuildingDto building) {
		Complex complex = complexRepository.findBy(building.getComplexId());
		if (complex != null) {
			Building buildingEntity = new Building();
			buildingEntity.setComplex(complex);
			buildingEntity.setName(building.getName());
			buildingEntity = buildingRepository.save(buildingEntity);
			return new BuildingDto(buildingEntity);
		}
		throw new EntityNotFoundException();
	}

	public BuildingDto update(Long id, BuildingDto building) {
		Optional<Complex> complex = complexRepository.findByBuildingId(id);
		if (complex.isPresent()) {
			Building buildingEntity = buildingRepository.findBy(id);
			if (buildingEntity != null) {
				buildingEntity.setComplex(complex.get());
				buildingEntity.setName(building.getName());
				buildingEntity = buildingRepository.save(buildingEntity);
				return new BuildingDto(buildingEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		Building building = buildingRepository.findBy(id);
		if (building != null) {
			buildingRepository.remove(building);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public BuildingDto.WithFloors find(Long id) {
		Building buildingEntity = buildingRepository.findBy(id);

		if (buildingEntity != null) {
			return new BuildingDto.WithFloors(buildingEntity);
		}
		throw new EntityNotFoundException();
	}
}