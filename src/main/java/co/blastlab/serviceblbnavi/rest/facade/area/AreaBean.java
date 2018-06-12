package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.service.AreaService;
import co.blastlab.serviceblbnavi.utils.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaBean implements AreaFacade {

	@Inject
	private Logger logger;

	@Inject
	private AreaRepository areaRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private AreaService areaService;

	@Override
	public AreaDto create(AreaDto area) {
		logger.debug("Trying to create area {}", area);
		Area areaEntity = new Area();
		Floor floor = floorRepository.findOptionalById(area.getFloorId()).orElseThrow(EntityNotFoundException::new);
		return areaService.createOrUpdate(areaEntity, area, floor);
	}

	@Override
	public AreaDto update(Long id, AreaDto area) {
		logger.debug("Trying to update area {}", area);
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		Floor floor = floorRepository.findOptionalById(area.getFloorId()).orElseThrow(EntityNotFoundException::new);
		return areaService.createOrUpdate(areaEntity, area, floor);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove area id {}", id);
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaRepository.remove(areaEntity);
		logger.debug("Area removed");
		return Response.noContent().build();
	}

	@Override
	public List<AreaDto> findAll() {
		return areaRepository.findAll().stream().map(AreaDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AreaDto> findAllByFloor(Long floorId) {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Area> areas = areaRepository.findByFloor(floor);
		return areas.stream().map(AreaDto::new).collect(Collectors.toList());
	}
}
