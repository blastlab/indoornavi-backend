package co.blastlab.serviceblbnavi.rest.facade.configuration;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Scale;
import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static co.blastlab.serviceblbnavi.domain.Scale.scale;

@Stateless
public class ConfigurationBean implements ConfigurationFacade {

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Override
	public ConfigurationDto publish(ConfigurationDto configuration) throws IOException {
		final Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		extractAnchors(configuration, floor);
		extractScale(configuration, floor);

		Integer latestVersion = Optional.ofNullable(configurationRepostiory.getLatestVersion(floor)).orElse(0);
		configuration.setVersion(latestVersion + 1);
		ObjectMapper objectMapper = new ObjectMapper();
		Configuration configurationEntity = configurationRepostiory.save(
			new Configuration(floor, latestVersion + 1, objectMapper.writeValueAsString(configuration))
		);
		return objectMapper.readValue(configurationEntity.getData(), ConfigurationDto.class);
	}

	private void extractScale(ConfigurationDto configuration, Floor floor) {
		ScaleDto scaleDto = configuration.getScale();
		Scale scale = scale(floor.getScale())
			.measure(scaleDto.getMeasure())
			.distance(scaleDto.getRealDistance())
			.startX(scaleDto.getStart().getX())
			.startY(scaleDto.getStart().getY())
			.stopX(scaleDto.getStop().getX())
			.stopY(scaleDto.getStop().getY());
		floor.setScale(scale);
		floorRepository.save(floor);
	}

	private void extractAnchors(ConfigurationDto configuration, Floor floor) {
		configuration.getAnchors().forEach((anchorDto) -> {
			Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			anchor.setFloor(floor);
			anchor.setX(anchorDto.getX());
			anchor.setY(anchorDto.getY());
			anchorRepository.save(anchor);
		});
	}

	@Override
	public ConfigurationDto saveDraft(ConfigurationDto configuration) throws JsonProcessingException {
		Floor floor = floorRepository.findOptionalById(configuration.getFloorId()).orElseThrow(EntityNotFoundException::new);
		Configuration configurationEntity = configurationRepostiory.findTopByFloorOrderByVersionDesc(floor).orElseThrow(EntityNotFoundException::new);
		ObjectMapper objectMapper = new ObjectMapper();
		configurationEntity.setData(objectMapper.writeValueAsString(configuration));
		configurationRepostiory.save(configurationEntity);
		return configuration;
	}

	@Override
	public List<ConfigurationDto> getAllOrderedByVersionDescending(Long floorId) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Configuration> configurations = configurationRepostiory.findByFloorOrderByVersionDesc(floor);
		List<ConfigurationDto> configurationDtos = new ArrayList<>();
		for (Configuration configuration : configurations) {
			configurationDtos.add(objectMapper.readValue(configuration.getData(), ConfigurationDto.class));
		}
		return configurationDtos;
	}
}
