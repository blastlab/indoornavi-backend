package co.blastlab.serviceblbnavi.rest.facade.configuration;

import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Publication;
import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import co.blastlab.serviceblbnavi.utils.ConfigurationExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class ConfigurationBean implements ConfigurationFacade {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationBean.class);

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
		LOGGER.debug("Trying to publish configuration of the floor id {}", floorId);
		final Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		if (publications.isEmpty()) {
			LOGGER.debug("No publications yet. Trying to create one");
			Publication publication = new Publication();
			publication.getFloors().add(floor);
			publication.setTags(tagRepository.findAll());
			publication.setUsers(userRepository.findAll());
			publicationRepository.save(publication);
		}
		Configuration configurationEntity = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ObjectMapper objectMapper = new ObjectMapper();
		ConfigurationDto.Data configurationData = objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);

		LOGGER.debug("Configuration data to extract: {}", configurationData);

		configurationExtractor.resetAnchors(floor);
		configurationExtractor.resetSinks(floor);
		configurationExtractor.resetAreas(floor);

		configurationExtractor.extractSinks(configurationData, floor);
		configurationExtractor.extractAnchors(configurationData, floor);
		configurationExtractor.extractScale(configurationData, floor);
		configurationExtractor.extractAreas(configurationData, floor);

		configurationEntity.setPublishedDate(new Date());
		return objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto.Data saveDraft(ConfigurationDto configuration) throws IOException {
		LOGGER.debug("Trying to save draft {}", configuration);
		Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		Optional<Configuration> configurationOptional = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor);
		Configuration latestConfiguration = configurationOptional.orElse(new Configuration(floor, 0));
		ObjectMapper objectMapper = new ObjectMapper();
		if (latestConfiguration.getPublishedDate() != null) {
			LOGGER.debug("Creating new draft because last one has been published {}", latestConfiguration.getPublishedDate());
			Integer latestVersion = configurationRepostiory.getLatestVersion(floor);
			latestConfiguration = new Configuration(
				floor,
				latestVersion + 1,
				objectMapper.writeValueAsString(configuration.getData()),
				null,
				new Date()
			);
		} else {
			LOGGER.debug("Updating previously created draft");
			latestConfiguration.setSaveDraftDate(new Date());
			latestConfiguration.setData(objectMapper.writeValueAsString(configuration.getData()));
		}
		latestConfiguration = configurationRepostiory.save(latestConfiguration);
		LOGGER.debug("Draft saved");
		return objectMapper.readValue(latestConfiguration.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto undo(Long floorId) throws IOException {
		LOGGER.debug("Trying to undo configuration to previous state");
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);

		List<Configuration> latestConfigurations = configurationRepostiory.findByFloorOrderByVersionDesc(floor);

		if (latestConfigurations.size() == 0) {
			throw new EntityNotFoundException();
		} else {
			Configuration configuration = latestConfigurations.get(0);
			if (latestConfigurations.size() == 1) {
				LOGGER.debug("There is only one configuration, so it's inital state");
				ObjectMapper objectMapper = new ObjectMapper();
				configuration.setPublishedDate(null);
				configuration.setSaveDraftDate(null);
				configuration.setData(objectMapper.writeValueAsString(new ConfigurationDto.Data()));
				configurationRepostiory.save(configuration);
				return new ConfigurationDto(configuration);
			} else {
				LOGGER.debug("Removing previous configuration");
				configurationRepostiory.remove(configuration);
				return new ConfigurationDto(latestConfigurations.get(1));
			}
		}
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
