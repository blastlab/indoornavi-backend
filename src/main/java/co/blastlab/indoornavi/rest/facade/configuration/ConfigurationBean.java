package co.blastlab.indoornavi.rest.facade.configuration;

import co.blastlab.indoornavi.dao.repository.*;
import co.blastlab.indoornavi.domain.Configuration;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Publication;
import co.blastlab.indoornavi.dto.configuration.ConfigurationDto;
import co.blastlab.indoornavi.dto.configuration.PrePublishReport;
import co.blastlab.indoornavi.utils.ConfigurationExtractor;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class ConfigurationBean implements ConfigurationFacade {

	@Inject
	private Logger logger;

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

	@Inject
	private ObjectMapper objectMapper;

	@Override
	public PrePublishReport prePublish(Long floorId) throws IOException {
		logger.debug("Doing pre publish checks configuration of the floor id {}", floorId);
		final Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		Configuration configurationEntity = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ConfigurationDto.Data configurationData = objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);

		PrePublishReport report = new PrePublishReport();
		logger.debug("Checking if devices are published in a different floor");
		configurationData.getSinks().forEach((sinkDto) -> {
			configurationExtractor.checkIsAnchorAlreadyPublishedOnDiffMap(sinkDto, floor, report);
			sinkDto.getAnchors().forEach((anchorDto -> {
				configurationExtractor.checkIsAnchorAlreadyPublishedOnDiffMap(anchorDto, floor, report);
			}));
		});

		return report;
	}

	@Override
	public ConfigurationDto.Data publish(Long floorId) throws IOException {
		logger.debug("Trying to publish configuration of the floor id {}", floorId);
		final Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		if (publications.isEmpty()) {
			logger.debug("No publications yet. Trying to create one");
			Publication publication = new Publication();
			publication.getFloors().add(floor);
			publication.setTags(tagRepository.findAll());
			publication.setUsers(userRepository.findAll());
			publicationRepository.save(publication);
		}
		Configuration configurationEntity = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ConfigurationDto.Data configurationData = objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);

		logger.debug("Configuration data to extract: {}", configurationData);

		configurationExtractor.resetAnchors(floor);
		configurationExtractor.resetSinks(floor);
		configurationExtractor.resetAreas(floor);

		configurationExtractor.extractSinks(configurationData, floor);
		configurationExtractor.extractScale(configurationData, floor);
		configurationExtractor.extractAreas(configurationData, floor);

		configurationEntity.setPublishedDate(new Date());

		if (!evictCacheInCalculator()) {
			logger.debug("Evict cache in calculator returned unexpected httpCode");
		}

		return objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto.Data saveDraft(ConfigurationDto configuration) throws IOException {
		logger.debug("Trying to save draft {}", configuration);
		Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		Optional<Configuration> configurationOptional = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor);
		Configuration latestConfiguration = configurationOptional.orElse(new Configuration(floor, 0));
		ObjectMapper objectMapper = new ObjectMapper();
		if (latestConfiguration.getPublishedDate() != null) {
			logger.debug("Creating new draft because last one has been published {}", latestConfiguration.getPublishedDate());
			Integer latestVersion = configurationRepostiory.getLatestVersion(floor);
			latestConfiguration = new Configuration(
				floor,
				latestVersion + 1,
				objectMapper.writeValueAsString(configuration.getData()),
				null,
				new Date()
			);
		} else {
			logger.debug("Updating previously created draft");
			latestConfiguration.setSaveDraftDate(new Date());
			latestConfiguration.setData(objectMapper.writeValueAsString(configuration.getData()));
		}
		latestConfiguration = configurationRepostiory.save(latestConfiguration);
		logger.debug("Draft saved");
		return objectMapper.readValue(latestConfiguration.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto undo(Long floorId) throws IOException {
		logger.debug("Trying to undo configuration to previous state");
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);

		List<Configuration> latestConfigurations = configurationRepostiory.findByFloorOrderByVersionDesc(floor);

		if (latestConfigurations.size() == 0) {
			throw new EntityNotFoundException();
		} else {
			Configuration configuration = latestConfigurations.get(0);
			if (latestConfigurations.size() == 1) {
				logger.debug("There is only one configuration, so it's initial state");
				ObjectMapper objectMapper = new ObjectMapper();
				configuration.setPublishedDate(null);
				configuration.setSaveDraftDate(null);
				configuration.setData(objectMapper.writeValueAsString(new ConfigurationDto.Data()));
				configurationRepostiory.save(configuration);
				return new ConfigurationDto(configuration);
			} else {
				logger.debug("Removing previous configuration");
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

	private boolean evictCacheInCalculator() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://calculator:8081/clearCache");
		Response response = target.request().post(Entity.json(""));
		return response.getStatus() == HttpResponseCodes.SC_NO_CONTENT;
	}
}
