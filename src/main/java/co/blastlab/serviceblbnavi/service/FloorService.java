package co.blastlab.serviceblbnavi.service;

import co.blastlab.serviceblbnavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.PublicationRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Publication;
import co.blastlab.serviceblbnavi.domain.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class FloorService {
	private final static Logger LOGGER = LoggerFactory.getLogger(FloorService.class);

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Inject
	private PublicationRepository publicationRepository;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private FloorRepository floorRepository;

	public void remove(Floor floor) {
		List<Configuration> configurations = configurationRepostiory.findByFloor(floor);
		LOGGER.debug("Removing all configurations: {}", configurations.size());
		for (Configuration configuration : configurations) {
			LOGGER.debug("Removing configuration {}", configuration);
			configurationRepostiory.remove(configuration);
		}
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		for (Publication publication : publications) {
			// if this is the last floor in this publication, we have to remove it
			if (publication.getFloors().size() == 1) {
				LOGGER.debug("It was the last floor in this publication {}, so removing it", publication);
				publicationRepository.remove(publication);
			}
		}
		List<Sink> sinks = sinkRepository.findByFloor(floor);
		sinks.forEach(sink -> {
			sink.unassign();
			sinkRepository.save(sink);
		});
		floorRepository.remove(floor);
	}
}
