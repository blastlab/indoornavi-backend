package pl.indoornavi.coordinatescalculator.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.indoornavi.coordinatescalculator.models.AnchorDto;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;

import java.util.List;

public abstract class Algorithm3d {
	private static Logger logger = LoggerFactory.getLogger(Algorithm3d.class);

	protected abstract AnchorRepository getAnchorRepository();

	protected List<AnchorDto> getAnchors(List<Integer> connectedAnchors) throws NotEnoughAnchors {
		int connectedAnchorsCount = connectedAnchors.size();

		if (connectedAnchorsCount < 4) {
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());
		List<AnchorDto> anchors = getAnchorRepository().findByShortIdIn(connectedAnchors);
		anchors.forEach(anchorDto -> {
			logger.info("____________________________________________{}", anchorDto.getZ());
		});

		if (anchors.size() < 4) {
			logger.trace("Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		return anchors;
	}

	class NotEnoughAnchors extends Exception {}
}
