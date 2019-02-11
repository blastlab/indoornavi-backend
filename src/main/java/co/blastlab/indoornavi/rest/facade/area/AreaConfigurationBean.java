package co.blastlab.indoornavi.rest.facade.area;

import co.blastlab.indoornavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.AreaConfiguration;
import co.blastlab.indoornavi.dto.area.AreaConfigurationDto;
import co.blastlab.indoornavi.utils.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaConfigurationBean implements AreaConfigurationFacade {

	@Inject
	private Logger logger;

	@Inject
	private AreaConfigurationRepository areaConfigurationRepository;

	@Inject
	private TagRepository tagRepository;

	@Override
	public AreaConfigurationDto create(AreaConfigurationDto areaConfiguration) {
		logger.debug("Trying to create area configuraton {}", areaConfiguration);
		AreaConfiguration areaConfigurationEntity = new AreaConfiguration();
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	public AreaConfigurationDto update(Long id, AreaConfigurationDto areaConfiguration) {
		logger.debug("Trying to update area configuraton {}", areaConfiguration);
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove area configuration id {}", id);
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaConfigurationRepository.remove(areaConfigurationEntity);
		logger.debug("Area configuration removed");
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
		logger.debug("Area configuration created/updated");
		return new AreaConfigurationDto(savedEntity);
	}
}
