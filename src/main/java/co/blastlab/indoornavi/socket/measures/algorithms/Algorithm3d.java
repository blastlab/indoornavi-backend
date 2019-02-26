package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Algorithm3d {
	@Inject
	private AnchorRepository anchorRepository;
	@Inject
	private Logger logger;

	protected List<Anchor> getAnchors(List<Integer> connectedAnchors) throws NotEnoughAnchors {
		int N = connectedAnchors.size();

		if (N < 4) {
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());

		List<Anchor> anchors = new ArrayList<>();
		for (Integer connectedAnchorShortId : connectedAnchors) {
			Optional<Anchor> anchorOptional = anchorRepository.findByShortId(connectedAnchorShortId);
			anchorOptional.ifPresent(anchors::add);
		}

		if (anchors.size() < 4) {
			logger.trace("Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		return anchors;
	}

	class NotEnoughAnchors extends Exception {}
}
