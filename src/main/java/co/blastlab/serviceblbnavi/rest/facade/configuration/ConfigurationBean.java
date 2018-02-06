package co.blastlab.serviceblbnavi.rest.facade.configuration;

import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Publication;
import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import co.blastlab.serviceblbnavi.utils.ConfigurationExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class ConfigurationBean implements ConfigurationFacade {

	@Inject
	private ConfigurationExtractor configurationExtractor;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Inject
	private PublicationRepository publicationRepository;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public ConfigurationDto.Data publish(Long floorId) throws IOException {
		final Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		if (publications.isEmpty()) {
			Publication publication = new Publication();
			publication.getFloors().add(floor);
			publication.setTags(tagRepository.findAll());
			publication.setUsers(userRepository.findAll());
			publicationRepository.save(publication);
		}
		Configuration configurationEntity = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ObjectMapper objectMapper = new ObjectMapper();
		ConfigurationDto.Data configurationData = objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);

		configurationExtractor.resetAnchors(floor);
		configurationExtractor.resetSinks(floor);
		configurationExtractor.resetAreas(floor);
		configurationExtractor.extractSinks(configurationData, floor);
		configurationExtractor.extractAnchors(configurationData, floor);
		configurationExtractor.extractScale(configurationData, floor);
		configurationExtractor.extractAreas(configurationData, floor);

		configurationEntity.setPublished(true);
		return objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto.Data saveDraft(ConfigurationDto configuration) throws IOException {
		Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		Optional<Configuration> configurationOptional = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor);
		Configuration latestConfiguration = configurationOptional.orElse(new Configuration(floor, 0, false));
		ObjectMapper objectMapper = new ObjectMapper();
		if (latestConfiguration.getPublished()) {
			Integer latestVersion = configurationRepostiory.getLatestVersion(floor);
			latestConfiguration = new Configuration(floor, latestVersion + 1, objectMapper.writeValueAsString(configuration.getData()), false);
		} else {
			latestConfiguration.setData(objectMapper.writeValueAsString(configuration.getData()));
		}
		latestConfiguration = configurationRepostiory.save(latestConfiguration);
		return objectMapper.readValue(latestConfiguration.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto undo(Long floorId) throws IOException {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);

		Optional<Configuration> configurationOptional = configurationRepostiory.findTop1ByFloorAndPublishedOrderByVersionDesc(floor, false);
		configurationOptional.ifPresent(configuration -> configurationRepostiory.remove(configuration));

		ObjectMapper objectMapper = new ObjectMapper();
		Configuration latestConfiguration = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor)
			.orElse(new Configuration(floor, 0, objectMapper.writeValueAsString(new ConfigurationDto.Data()),false));
		return new ConfigurationDto(latestConfiguration);
	}

	@Override
	public List<ConfigurationDto> getAllOrderedByVersionDescending(Long floorId) throws IOException {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Configuration> configurations = configurationRepostiory.findByFloorOrderByVersionDesc(floor);
		List<ConfigurationDto> configurationDtos = new ArrayList<>();
		for (Configuration configuration : configurations) {
			ConfigurationDto configurationDto = new ConfigurationDto(configuration);
			configurationDtos.add(configurationDto);
		}
		return configurationDtos;
	}
}
