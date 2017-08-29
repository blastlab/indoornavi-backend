package co.blastlab.serviceblbnavi.rest.facade.configuration;

import co.blastlab.serviceblbnavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
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

	@Override
	public ConfigurationDto.Data publish(Long floorId) throws IOException {
		final Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		Configuration configurationEntity = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ObjectMapper objectMapper = new ObjectMapper();
		ConfigurationDto.Data configurationData = objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.Data.class);

		configurationExtractor.extractSinks(configurationData, floor);
		configurationExtractor.extractScale(configurationData, floor);

		Integer latestVersion = configurationRepostiory.getLatestVersion(floor);
		Configuration newConfigurationEntity = configurationRepostiory.save(
			new Configuration(floor, latestVersion + 1, objectMapper.writeValueAsString(configurationData))
		);
		configurationRepostiory.save(newConfigurationEntity);
		return objectMapper.readValue(newConfigurationEntity.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public ConfigurationDto.Data saveDraft(ConfigurationDto configuration) throws IOException {
		Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		Optional<Configuration> configurationOptional = configurationRepostiory.findTop1ByFloorOrderByVersionDesc(floor);
		Configuration latestConfiguration = configurationOptional.orElse(new Configuration(floor, 0));
		ObjectMapper objectMapper = new ObjectMapper();
		latestConfiguration.setData(objectMapper.writeValueAsString(configuration.getData()));
		latestConfiguration = configurationRepostiory.save(latestConfiguration);
		return objectMapper.readValue(latestConfiguration.getData(), ConfigurationDto.Data.class);
	}

	@Override
	public List<ConfigurationDto> getAllOrderedByVersionDescending(Long floorId) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Configuration> configurations = configurationRepostiory.findByFloorOrderByVersionDesc(floor);
		List<ConfigurationDto> configurationDtos = new ArrayList<>();
		for (Configuration configuration : configurations) {
			ConfigurationDto configurationDto = new ConfigurationDto();
			configurationDto.setVersion(configuration.getVersion());
			configurationDto.setFloorId(configuration.getFloor().getId());
			configurationDto.setData(objectMapper.readValue(configuration.getData(), ConfigurationDto.Data.class));
			configurationDtos.add(configurationDto);
		}
		return configurationDtos;
	}
}
