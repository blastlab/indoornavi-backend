package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaBean implements AreaFacade {

	@Inject
	private AreaRepository areaRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private AreaConfigurationRepository areaConfigurationRepository;

	@Override
	// TODO: AutorizedAccess
	public AreaDto create(AreaDto area) {
		Area areaEntity = new Area();
		return createOrUpdate(areaEntity, area);
	}

	@Override
	// TODO: AutorizedAccess
	public AreaDto update(Long id, AreaDto area) {
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(areaEntity, area);
	}

	@Override
	// TODO: AutorizedAccess
	public Response delete(Long id) {
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaRepository.remove(areaEntity);
		return Response.noContent().build();
	}

	@Override
	// TODO: AutorizedAccess
	public List<AreaDto> findAll() {
		return areaRepository.findAll().stream().map(AreaDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AreaDto> findAllByFloor(Long floorId) {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Area> areas = areaRepository.findByFloor(floor);
		return areas.stream().map(AreaDto::new).collect(Collectors.toList());
	}

	private AreaDto createOrUpdate(Area areaEntity, AreaDto area) {
		areaEntity.setName(area.getName());
		List<AreaConfiguration> areaConfigurations = area.getConfigurations().stream().map(id -> areaConfigurationRepository.findBy(id)).collect(Collectors.toList());
		areaEntity.setConfigurations(areaConfigurations);
		areaEntity.setPolygon(area.toPolygon());
		areaEntity = areaRepository.save(areaEntity);
		return new AreaDto(areaEntity);
	}
}
