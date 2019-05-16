package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.socket.LoggerController;
import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Algorithm3d {
	@Inject
	private AnchorRepository anchorRepository;
	@Inject
	private LoggerController logger;

	protected List<Anchor> getAnchors(String sessionId, List<Integer> connectedAnchors) throws NotEnoughAnchors {
		int connectedAnchorsCount = connectedAnchors.size();

		if (connectedAnchorsCount < 4) {
			logger.trace(sessionId,"Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		logger.trace(sessionId,"Connected anchors: {}", connectedAnchors.size());

		List<Anchor> anchors = anchorRepository.findByShortIdIn(connectedAnchors);

		if (anchors.size() < 4) {
			logger.trace(sessionId,"Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		return anchors;
	}

	class NotEnoughAnchors extends Exception {}
}
