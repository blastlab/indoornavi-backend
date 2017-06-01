package co.blastlab.serviceblbnavi.socket.bridge;

import co.blastlab.serviceblbnavi.dto.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class AnchorPositionBridge implements Bridge {
	private Integer sinkId;
	private Point sinkPosition;
	private Integer firstAnchorId;
	private Double firstAnchorDegree;
	private Double L10; // distance between sink and first anchor
	private Map<Integer, DistancePair> distancePairs = new HashMap<>();
	@Inject
	private Event<AnchorPoints> anchorPositionEvent;

	public void startListening(Integer sinkId, Integer firstAnchorId, Point sinkPosition, Double firstAnchorDegree) {
		this.sinkId = sinkId;
		this.firstAnchorId = firstAnchorId;
		this.sinkPosition = sinkPosition;
		this.firstAnchorDegree = firstAnchorDegree;
	}

	public void stopListening(Integer sinkId, Integer firstAnchorId) {
		if (this.sinkId.equals(sinkId) && this.firstAnchorId.equals(firstAnchorId)) {
			this.sinkId = null;
			this.firstAnchorId = null;
		}
	}

	@Override
	public void addDistance(Integer firstDeviceId, Integer secondDeviceId, Integer distance) throws UnrecognizedDeviceException {
		if (this.sinkId != null && this.firstAnchorId != null) {

			Integer sinkId = this.sinkId.equals(firstDeviceId) ? firstDeviceId : this.sinkId.equals(secondDeviceId) ? secondDeviceId : null;
			Integer firstAnchorId = this.firstAnchorId.equals(firstDeviceId) ? firstDeviceId : this.firstAnchorId.equals(secondDeviceId) ? secondDeviceId : null;
			Integer otherAnchorId;
			Double L21 = null;
			Double L20 = null;

			if (sinkId != null && firstAnchorId != null) {
				L10 = Double.valueOf(distance);
			} else {

				if (sinkId != null) {
					otherAnchorId = firstDeviceId.equals(sinkId) ? secondDeviceId : firstDeviceId;
					L20 = Double.valueOf(distance);
				} else if (firstAnchorId != null) {
					otherAnchorId = firstDeviceId.equals(firstAnchorId) ? secondDeviceId : firstDeviceId;
					L21 = Double.valueOf(distance);
				} else {
					throw new UnrecognizedDeviceException();
				}

				if (!distancePairs.containsKey(otherAnchorId)) {
					distancePairs.put(otherAnchorId, new DistancePair());
				} else {
					DistancePair distancePair = distancePairs.get(otherAnchorId);

					if (distancePair.getL20() == null && L20 != null) {
						distancePair.setL20(L20);
					} else if (distancePair.getL21() == null && L21 != null) {
						distancePair.setL21(L21);
					}

					if (distancePair.getL20() != null && distancePair.getL21() != null) {
						List<Point> points = calculateAnchorPositions(distancePair);
						anchorPositionEvent.fire(new AnchorPoints(otherAnchorId, points));
					}
				}

			}
		}
	}

	/*
		L21 - distance between first and second anchor
		L20 - distance between sink and second anchor
	 */
	List<Point> calculateAnchorPositions(DistancePair distancePair) {
		Double L20 = distancePair.getL20();
		Double L21 = distancePair.getL21();
		List<Point> points = new ArrayList<>();
		double X2 = L10 != null ? (int) ((L10 * L10 - L21 * L21 + L20 * L20) / (2 * L10)) : L20;
		Point point = new Point();
		point.setX((int) X2);
		point.setY((int) Math.sqrt(Math.abs(L20 * L20 - X2 * X2)));

		Point2D.Double mirrored = new Point2D.Double();
		Point2D.Double secondAnchorPosition = new Point2D.Double(point.getX(), point.getY());

		AffineTransform affineTransform = new AffineTransform();
		// mirror vs y axis
		affineTransform.setTransform(AffineTransform.getScaleInstance(1, -1));
		affineTransform.transform(secondAnchorPosition, mirrored);

		points.add(moveAndRotate(secondAnchorPosition));
		points.add(moveAndRotate(mirrored));

		return points;
	}

	private Point moveAndRotate(Point2D.Double toTransform) {
		AffineTransform affineTransform = new AffineTransform();
		Point2D.Double result = new Point2D.Double();
		double radians = (Math.toRadians(firstAnchorDegree));
		affineTransform.rotate(radians);
		affineTransform.translate(this.sinkPosition.getX(), this.sinkPosition.getY());
		affineTransform.transform(toTransform, result);
		return new Point((int) result.getX(), (int) result.getY());
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static class DistancePair {
		private Double L20;
		private Double L21;
	}
}
