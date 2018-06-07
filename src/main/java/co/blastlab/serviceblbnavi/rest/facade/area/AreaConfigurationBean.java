package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.dto.area.AreaConfigurationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaConfigurationBean implements AreaConfigurationFacade {

	private final static Logger LOGGER = LoggerFactory.getLogger(AreaConfigurationBean.class);

	@Inject
	private AreaConfigurationRepository areaConfigurationRepository;

	@Inject
	private TagRepository tagRepository;

	@Override
	public AreaConfigurationDto create(AreaConfigurationDto areaConfiguration) {
		LOGGER.debug("Trying to create area configuraton {}", areaConfiguration);
		AreaConfiguration areaConfigurationEntity = new AreaConfiguration();
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	public AreaConfigurationDto update(Long id, AreaConfigurationDto areaConfiguration) {
		LOGGER.debug("Trying to update area configuraton {}", areaConfiguration);
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	public Response delete(Long id) {
		LOGGER.debug("Trying to remove area configuration id {}", id);
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaConfigurationRepository.remove(areaConfigurationEntity);
		LOGGER.debug("Area configuration removed");
		return Response.noContent().build();
	}

	@Override
	public List<AreaConfigurationDto> findAll() {
		return areaConfigurationRepository.findAll().stream().map(AreaConfigurationDto::new).collect(Collectors.toList());
	}

	private AreaConfigurationDto createOrUpdate(AreaConfiguration areaConfigurationEntity, AreaConfigurationDto areaConfiguration) {
		areaConfigurationEntity.setOffset(areaConfiguration.getOffset());
		areaConfigurationEntity.setMode(areaConfiguration.getMode());
		areaConfigurationEntity.setTags(
			areaConfiguration.getTags().stream().map(tagDto -> tagRepository.findOptionalByShortId(tagDto.getShortId()).orElseThrow(EntityNotFoundException::new))
				.collect(Collectors.toList())
		);
		AreaConfiguration savedEntity = areaConfigurationRepository.save(areaConfigurationEntity);
		LOGGER.debug("Area configuration created/updated");
		return new AreaConfigurationDto(savedEntity);
	}
}
