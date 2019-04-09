package co.blastlab.indoornavi.service;

import co.blastlab.indoornavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.indoornavi.dao.repository.AreaRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Area;
import co.blastlab.indoornavi.domain.AreaConfiguration;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Tag;
import co.blastlab.indoornavi.dto.area.AreaDto;
import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaService {
	@Inject
	private Logger logger;
	@Inject
	private TagRepository tagRepository;
	@Inject
	private AreaRepository areaRepository;
	@Inject
	private AreaConfigurationRepository areaConfigurationRepository;

	public AreaDto createOrUpdate(Area areaEntity, AreaDto area, Floor floor) {
		areaEntity.setName(area.getName());
		List<AreaConfiguration> areaConfigurations = new ArrayList<>();
		area.getConfigurations().forEach((areaConfigurationDto) -> {
			Optional<AreaConfiguration> areaConfigurationOptional = areaConfigurationRepository.findOptionalById(areaConfigurationDto.getId());
			AreaConfiguration areaConfiguration = areaConfigurationOptional.orElse(new AreaConfiguration());
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
		areaEntity.setPolygon(area.toPolygon(false));
		areaEntity.setPolygonInPixels(area.toPolygon(true));
		areaEntity.setFloor(floor);
		areaEntity.setHMax(area.getHeightMax());
		areaEntity.setHMin(area.getHeightMin());
		areaEntity = areaRepository.save(areaEntity);
		logger.debug("Area created/updated");
		return new AreaDto(areaEntity);
	}
}
