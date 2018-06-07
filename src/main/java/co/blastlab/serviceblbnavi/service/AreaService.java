package co.blastlab.serviceblbnavi.service;

import co.blastlab.serviceblbnavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaService {
	private final static Logger LOGGER = LoggerFactory.getLogger(AreaService.class);

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
		areaEntity.setPolygon(area.toPolygon());
		areaEntity.setFloor(floor);
		areaEntity = areaRepository.save(areaEntity);
		LOGGER.debug("Area created/updated");
		return new AreaDto(areaEntity);
	}
}
