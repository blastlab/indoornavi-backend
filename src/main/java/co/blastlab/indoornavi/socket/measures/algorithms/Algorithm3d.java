package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.socket.LoggerController;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Algorithm3d {
	@Inject
	private AnchorRepository anchorRepository;
//	@Inject
//	private LoggerController logger;

	@Inject
	private EntityManager entityManager;

	private static Logger logger = LoggerFactory.getLogger("TEST");

	protected List<Anchor> getAnchors(List<Integer> connectedAnchors) throws NotEnoughAnchors {
		int connectedAnchorsCount = connectedAnchors.size();

		if (connectedAnchorsCount < 4) {
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());
		List<Anchor> anchors = anchorRepository.findByShortIdIn(connectedAnchors);

		if (anchors.size() < 4) {
			logger.trace("Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			throw new NotEnoughAnchors();
		}

		return anchors;
	}

	class NotEnoughAnchors extends Exception {}
}
