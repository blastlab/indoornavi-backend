package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaBean implements AreaFacade {

	@Inject
	private AreaRepository areaRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private TagRepository tagRepository;

	@Override
	@AuthorizedAccess("FLOOR_UPDATE")
	public AreaDto create(AreaDto area) {
		Area areaEntity = new Area();
		return createOrUpdate(areaEntity, area);
	}

	@Override
	@AuthorizedAccess("FLOOR_UPDATE")
	public AreaDto update(Long id, AreaDto area) {
		Area areaEntity = areaRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(areaEntity, area);
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

	private AreaDto createOrUpdate(Area areaEntity, AreaDto area) {
		areaEntity.setName(area.getName());
		List<AreaConfiguration> areaConfigurations = new ArrayList<>();
		area.getConfigurations().forEach((areaConfigurationDto) -> {
			AreaConfiguration areaConfiguration = new AreaConfiguration();
			areaConfiguration.setMode(areaConfigurationDto.getMode());
			areaConfiguration.setOffset(areaConfigurationDto.getOffset());
			List<Tag> tags = new ArrayList<>();
			areaConfigurationDto.getTags().forEach((tagDto) -> {
				Tag tag = tagRepository.findOptionalByShortId(tagDto.getShortId()).orElseThrow(EntityNotFoundException::new);
				tags.add(tag);
			});
			areaConfiguration.setTags(tags);
			areaConfigurations.add(areaConfiguration);
		});
		areaEntity.setConfigurations(areaConfigurations);
		areaEntity.setPolygon(area.toPolygon());
		Floor floor = floorRepository.findOptionalById(area.getFloorId()).orElseThrow(EntityNotFoundException::new);
		areaEntity.setFloor(floor);
		areaEntity = areaRepository.save(areaEntity);
		return new AreaDto(areaEntity);
	}
}
