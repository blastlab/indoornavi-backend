package co.blastlab.indoornavi.rest.facade.path;

import co.blastlab.indoornavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.domain.Configuration;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.configuration.ConfigurationDto;
import co.blastlab.indoornavi.dto.path.PathDto;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

public class PathBean implements PathFacade {

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private Logger logger;

	@Override
	public List<PathDto> getPaths(Long floorId) {
		ObjectMapper objectMapper = new ObjectMapper();
		logger.debug("Trying to get floor {}", floorId);
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		logger.debug("Trying to get latest configuration");
		Configuration configuration = configurationRepostiory
			.findTop1ByFloorAndPublishedDateIsNotNullOrderByVersionDesc(floor)
			.orElseThrow(EntityNotFoundException::new);
		try {
			logger.debug("Trying to read data from configration json string {}", configuration.getData());
			final ConfigurationDto.Data data = objectMapper.readValue(configuration.getData(), ConfigurationDto.Data.class);
			return data.getPaths();
		} catch (IOException e) {
			logger.debug("Error occured while trying to read json string {}", configuration.getData());
			e.printStackTrace();
			return null;
		}
	}
}
