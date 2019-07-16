package pl.indoornavi.coordinatescalculator.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.indoornavi.coordinatescalculator.models.Anchor;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;

import java.util.List;

public abstract class Algorithm3d {
	private static Logger logger = LoggerFactory.getLogger(Algorithm3d.class);

	protected abstract AnchorRepository getAnchorRepository();

	protected List<Anchor> getAnchors(List<Integer> connectedAnchors) throws NotEnoughAnchors {
		int connectedAnchorsCount = connectedAnchors.size();

		if (connectedAnchorsCount < 4) {
			log("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		log("Connected anchors: {}", connectedAnchorsCount);
		List<Anchor> anchors = getAnchorRepository().findByShortIdIn(connectedAnchors);
		int dbConnectedAnchorsCount = anchors.size();

		if (dbConnectedAnchorsCount < 4) {
			log("Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", dbConnectedAnchorsCount);
			throw new NotEnoughAnchors();
		}

		return anchors;
	}

	private void log(String message, Object... args) {
		if (logger.isTraceEnabled()) {
			logger.trace(message, args);
		}
	}

	class NotEnoughAnchors extends Exception {}
}
