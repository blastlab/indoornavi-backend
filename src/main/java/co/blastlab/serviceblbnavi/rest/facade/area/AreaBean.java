package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.service.AreaService;

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
	private AreaService areaService;

	@Override
	@AuthorizedAccess("FLOOR_UPDATE")
	public AreaDto create(AreaDto area) {
		Area areaEntity = new Area();
		Floor floor = floorRepository.findOptionalById(area.getFloorId()).orElseThrow(EntityNotFoundException::new);
		return areaService.createOrUpdate(areaEntity, area, floor);
	}

	@Override
	@AuthorizedAccess("FLOOR_UPDATE")
	public AreaDto update(Long id, AreaDto area) {
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		Floor floor = floorRepository.findOptionalById(area.getFloorId()).orElseThrow(EntityNotFoundException::new);
		return areaService.createOrUpdate(areaEntity, area, floor);
	}

	@Override
	@AuthorizedAccess("FLOOR_UPDATE")
	public Response delete(Long id) {
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaRepository.remove(areaEntity);
		return Response.noContent().build();
	}

	@Override
	@AuthorizedAccess("FLOOR_READ")
	public List<AreaDto> findAll() {
		return areaRepository.findAll().stream().map(AreaDto::new).collect(Collectors.toList());
	}

	@Override
	@AuthorizedAccess("FLOOR_READ")
	public List<AreaDto> findAllByFloor(Long floorId) {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Area> areas = areaRepository.findByFloor(floor);
		return areas.stream().map(AreaDto::new).collect(Collectors.toList());
	}
}
