package co.blastlab.indoornavi.socket.bridge;

import co.blastlab.indoornavi.utils.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SinkAnchorsDistanceBridge implements Bridge {

	@Inject
	private Logger logger;

	private Integer sinkId;

	private Map<Integer, List<Integer>> distancesPerAnchor = new HashMap<>();

	@Inject
	private Event<AnchorDistance> anchorDistanceEvent;

	public void startListening(Integer sinkId) {
		this.sinkId = sinkId;
	}

	public void stopListening(Integer sinkId) {
		if (this.sinkId.equals(sinkId)) {
			this.sinkId = null;
			this.distancesPerAnchor.clear();
		}
	}

	/**
	 * When anchor distance is not null then event is fired to:
	 * {@link co.blastlab.indoornavi.socket.wizard.WizardWebSocket#anchorDistanceAdded(AnchorDistance)}
	 */
	@Override
	public void addDistance(Integer firstDevice, Integer secondDevice, Integer distance) {
		if (this.sinkId != null) {
			logger.trace(
				"Trying to calculate distance between sink and connected anchors. Received: first device = {}, second device = {}, distance = {}",
				firstDevice, secondDevice, distance
			);
			AnchorDistance anchorDistance = null;
			if (firstDevice.equals(this.sinkId)) {
				anchorDistance = new AnchorDistance(secondDevice, distance);
			} else if (secondDevice.equals(this.sinkId)) {
				anchorDistance = new AnchorDistance(firstDevice, distance);
			}
			if (anchorDistance != null) {
				if (distancesPerAnchor.containsKey(anchorDistance.getAnchorId())) {
					distancesPerAnchor.get(anchorDistance.getAnchorId()).add(anchorDistance.getDistance());
				} else {
					distancesPerAnchor.put(anchorDistance.getAnchorId(), new ArrayList<>(Collections.singletonList(anchorDistance.getDistance())));
				}
				logger.trace("Got {} distances for anchor id {}", distancesPerAnchor.size(), anchorDistance.getAnchorId());
				if (distancesPerAnchor.get(anchorDistance.getAnchorId()).size() == 10) {
					logger.trace("Got all required distances for anchor id {}", anchorDistance.getAnchorId());
					anchorDistance.setDistance(
						distancesPerAnchor.get(anchorDistance.getAnchorId()).stream().mapToInt(Integer::intValue).sum() / 10
					);
					anchorDistanceEvent.fire(anchorDistance);
				}
			}
		}
	}
}
