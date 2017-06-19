package co.blastlab.serviceblbnavi.rest.facade.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaConfigurationRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.dto.area.AreaConfigurationDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AreaConfigurationBean implements AreaConfigurationFacade {

	@Inject
	private AreaConfigurationRepository areaConfigurationRepository;

	@Inject
	private TagRepository tagRepository;

	@Override
	// TODO: AutorizedAccess
	public AreaConfigurationDto create(AreaConfigurationDto areaConfiguration) {
		AreaConfiguration areaConfigurationEntity = new AreaConfiguration();
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	// TODO: AutorizedAccess
	public AreaConfigurationDto update(Long id, AreaConfigurationDto areaConfiguration) {
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(areaConfigurationEntity, areaConfiguration);
	}

	@Override
	// TODO: AutorizedAccess
	public Response delete(Long id) {
		AreaConfiguration areaConfigurationEntity = areaConfigurationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		areaConfigurationRepository.remove(areaConfigurationEntity);
		return Response.noContent().build();
	}

	@Override
	// TODO: AutorizedAccess
	public List<AreaConfigurationDto> findAll() {
		return areaConfigurationRepository.findAll().stream().map(AreaConfigurationDto::new).collect(Collectors.toList());
	}

	private AreaConfigurationDto createOrUpdate(AreaConfiguration areaConfigurationEntity, AreaConfigurationDto areaConfiguration) {
		areaConfigurationEntity.setOffset(areaConfiguration.getOffset());
		areaConfigurationEntity.setMode(areaConfiguration.getMode());
		areaConfigurationEntity.setTags(
			areaConfiguration.getTags().stream().map(tagShortId -> tagRepository.findOptionalByShortId(tagShortId).orElseThrow(EntityNotFoundException::new))
				.collect(Collectors.toList())
		);
		AreaConfiguration savedEntity = areaConfigurationRepository.save(areaConfigurationEntity);
		return new AreaConfigurationDto(savedEntity);
	}
}
