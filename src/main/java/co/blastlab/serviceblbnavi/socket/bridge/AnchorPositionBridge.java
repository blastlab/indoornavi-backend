package co.blastlab.serviceblbnavi.socket.bridge;

import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.utils.Logger;
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
	@Inject
	private Logger logger;

	private Integer sinkId;
	private Point sinkPosition;
	private Integer firstAnchorId;
	private Double firstAnchorDegree;
	/**
	 * distance between sink and first anchor
	 */
	private Double L10;
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

	/**
	 * When the distance pair gets fullfilled the event with anchor points is fired to:
	 {@link co.blastlab.serviceblbnavi.socket.wizard.WizardWebSocket#anchorPointsCalculated(AnchorPoints)}
	 */
	@Override
	public void addDistance(Integer firstDeviceId, Integer secondDeviceId, Integer distance) throws UnrecognizedDeviceException {
		if (this.sinkId != null && this.firstAnchorId != null) {

			logger.trace(
				"Trying to find anchors' distances. Received first device = {}, second device = {}, distance = {}",
				firstDeviceId, secondDeviceId, distance
			);

			Integer sinkId = getSinkId(firstDeviceId, secondDeviceId);
			Integer firstAnchorId = getFirstAnchorId(firstDeviceId, secondDeviceId);
			Integer otherAnchorId;
			Double L21 = null;
			Double L20 = null;

			if (sinkId != null && firstAnchorId != null) {
				L10 = Double.valueOf(distance);
				logger.trace("Found distance between first anchor and sink {}", distance);
			} else {

				if (sinkId != null) {
					otherAnchorId = firstDeviceId.equals(sinkId) ? secondDeviceId : firstDeviceId;
					L20 = Double.valueOf(distance);
					logger.trace("Found distance between second anchor and sink {}", distance);
				} else if (firstAnchorId != null) {
					otherAnchorId = firstDeviceId.equals(firstAnchorId) ? secondDeviceId : firstDeviceId;
					L21 = Double.valueOf(distance);
					logger.trace("Found distance between second anchor and first anchor {}", distance);
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
						logger.trace("Found all distances, trying to calculate positions");
						List<Point> points = calculateAnchorPositions(distancePair);
						logger.trace("Calculated points {}, {}", points.get(0), points.get(1));
						anchorPositionEvent.fire(new AnchorPoints(otherAnchorId, points));
					}
				}

			}
		}
	}

	private Integer getSinkId(Integer firstDeviceId, Integer secondDeviceId) {
		return this.sinkId.equals(firstDeviceId) ? firstDeviceId : this.sinkId.equals(secondDeviceId) ? secondDeviceId : null;
	}

	private Integer getFirstAnchorId(Integer firstDeviceId, Integer secondDeviceId) {
		return this.firstAnchorId.equals(firstDeviceId) ? firstDeviceId : this.firstAnchorId.equals(secondDeviceId) ? secondDeviceId : null;
	}

	List<Point> calculateAnchorPositions(DistancePair distancePair) {
		logger.trace("Trying to calculate second anchor position");
		Point point = calculateSecondAnchorPosition(distancePair);
		logger.trace("Second anchor position is {}", point);

		Point2D.Double mirrored = new Point2D.Double();
		Point2D.Double secondAnchorPosition = new Point2D.Double(point.getX(), point.getY());

		AffineTransform affineTransform = new AffineTransform();
		// mirror vs y axis
		affineTransform.setTransform(AffineTransform.getScaleInstance(1, -1));
		affineTransform.transform(secondAnchorPosition, mirrored);

		List<Point> points = new ArrayList<>();
		points.add(moveAndRotate(secondAnchorPosition));
		points.add(moveAndRotate(mirrored));

		return points;
	}

	/**
	 * Calculate initial second anchor position
	 * @param distancePair it's distance between sink and second anchor and between first anchor and second anchor
	 * @return calculated initial position
	 */
	private Point calculateSecondAnchorPosition(DistancePair distancePair) {
		Double L20 = distancePair.getL20();
		Double L21 = distancePair.getL21();

		double X2 = L10 != null ? (int) ((L10 * L10 - L21 * L21 + L20 * L20) / (2 * L10)) : L20;
		Point point = new Point();
		point.setX((int) X2);
		point.setY((int) Math.sqrt(Math.abs(L20 * L20 - X2 * X2)));
		return point;
	}

	private Point moveAndRotate(Point2D.Double toTransform) {
		Point2D.Double rotationResult = rotate(toTransform);
		Point2D.Double moveResult = move(rotationResult);
		return new Point((int) moveResult.getX(), (int) moveResult.getY());
	}

	/**
	 * Rotate by given degree (it's between y axis and first anchor position)
	 * @param toTransform position of second anchor or it's mirrored position
	 * @return result of rotation
	 */
	private Point2D.Double rotate(Point2D.Double toTransform) {
		AffineTransform affineTransform = new AffineTransform();
		Point2D.Double rotationResult = new Point2D.Double();
		double radians = (Math.toRadians(firstAnchorDegree));
		affineTransform.rotate(radians);
		affineTransform.transform(toTransform, rotationResult);
		return rotationResult;
	}

	/**
	 * Move point according to difference between sink initial position (0, 0) and it's current position (set by user)
	 * @param toTransform rotated position of second anchor or it's mirrored position
	 * @return result of moving
	 */
	private Point2D.Double move(Point2D.Double toTransform) {
		AffineTransform affineTransform = new AffineTransform();
		Point2D.Double moveResult = new Point2D.Double();
		affineTransform.translate(this.sinkPosition.getX(), this.sinkPosition.getY());
		affineTransform.transform(toTransform, moveResult);
		return moveResult;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static class DistancePair {
		/*
		  distance between sink and second anchor
 		 */
		private Double L20;
		/*
		  distance between first and second anchor
 		 */
		private Double L21;
	}
}
