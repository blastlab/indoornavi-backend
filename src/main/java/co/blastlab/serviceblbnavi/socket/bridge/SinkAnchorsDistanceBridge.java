package co.blastlab.serviceblbnavi.socket.bridge;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SinkAnchorsDistanceBridge implements Bridge {
	private Integer sinkId;

	@Inject
	private Event<AnchorDistance> anchorDistanceEvent;

	public void startListening(Integer sinkId) {
		this.sinkId = sinkId;
	}

	public void stopListening(Integer sinkId) {
		if (this.sinkId.equals(sinkId)) {
			this.sinkId = null;
		}
	}

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
				anchorDistanceEvent.fire(anchorDistance);
			}
		}
	}

}
