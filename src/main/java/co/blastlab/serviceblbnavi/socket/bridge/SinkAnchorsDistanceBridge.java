package co.blastlab.serviceblbnavi.socket.bridge;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SinkAnchorsDistanceBridge implements Bridge {
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
	 {@link co.blastlab.serviceblbnavi.socket.wizard.WizardWebSocket#anchorDistanceAdded(AnchorDistance)}
	 */
	@Override
	public void addDistance(Integer firstDevice, Integer secondDevice, Integer distance) throws UnrecognizedDeviceException {
		if (this.sinkId != null) {
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
				if (distancesPerAnchor.get(anchorDistance.getAnchorId()).size() == 10) {
					anchorDistance.setDistance(
						distancesPerAnchor.get(anchorDistance.getAnchorId()).stream().mapToInt(Integer::intValue).sum() / 10
					);
					anchorDistanceEvent.fire(anchorDistance);
				}
			}
		}
	}

}
