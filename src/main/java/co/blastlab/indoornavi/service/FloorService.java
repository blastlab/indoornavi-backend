package co.blastlab.indoornavi.service;

import co.blastlab.indoornavi.dao.repository.ConfigurationRepostiory;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.PublicationRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.domain.Configuration;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Publication;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import java.util.List;

public class FloorService {
	@Inject
	private Logger logger;

	@Inject
	private ConfigurationRepostiory configurationRepostiory;

	@Inject
	private PublicationRepository publicationRepository;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private FloorRepository floorRepository;

	public void remove(Floor floor) {
		removeNoCommit(floor);
		floorRepository.remove(floor);
	}

	public void removeNoCommit(Floor floor ) {
		List<Configuration> configurations = configurationRepostiory.findByFloor(floor);
		logger.debug("Removing all configurations: {}", configurations.size());
		for (Configuration configuration : configurations) {
			logger.debug("Removing configuration {}", configuration);
			configurationRepostiory.remove(configuration);
		}
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		for (Publication publication : publications) {
			// if this is the last floor in this publication, we have to remove it
			if (publication.getFloors().size() == 1) {
				logger.debug("It was the last floor in this publication {}, so removing it", publication);
				publicationRepository.remove(publication);
			}
		}
		List<Sink> sinks = sinkRepository.findByFloor(floor);
		sinks.forEach(sink -> {
			sink.unassign();
			sinkRepository.save(sink);
		});
	}
}
