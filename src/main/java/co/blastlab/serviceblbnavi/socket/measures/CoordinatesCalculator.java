package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.socket.tagTracer.TagTraceDto;
import co.blastlab.serviceblbnavi.utils.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.*;

@Singleton
public class CoordinatesCalculator {

	// tag short id, anchor short id, measure list
	private Map<Integer, Map<Integer, List<Measure>>> measureStorage = new LinkedHashMap<>();

	// 10 seconds
	private final static long OLD_DATA_IN_MILISECONDS = 10_000;

	private final static int TAG_Z = 100;

	private Map<Integer, PointAndTime> previousCoorinates = new HashMap<>();

	@Inject
	private Logger logger;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	private boolean traceTags;

	@Inject
	private Event<TagTraceDto> tagTraceEvent;

	public void startTracingTags() {
		this.traceTags = true;
	}

	public void stopTracingTags() {
		this.traceTags = false;
	}

	public Optional<UwbCoordinatesDto> calculateTagPosition(int firstDeviceId, int secondDeviceId, int distance, boolean is3D) {
		logger.trace("Measure storage tags: {}", measureStorage.keySet().size());

		Integer tagId = getTagId(firstDeviceId, secondDeviceId);
		Integer anchorId = getAnchorId(firstDeviceId, secondDeviceId);
		if (tagId == null || anchorId == null) {
			logger.trace(String.format("One of the devices' ids is out of range. Ids are: %s, %s and range is (1, %s)", firstDeviceId, secondDeviceId, Short.MAX_VALUE));
			return Optional.empty();
		}

		setConnection(tagId, anchorId, distance);

		Set<Integer> connectedAnchors = getConnectedAnchors(tagId);

		Optional<Point3D> calculatedPointOptional = is3D ? calculate3d(connectedAnchors, tagId) : calculate2d(connectedAnchors, tagId);

		if (!calculatedPointOptional.isPresent()) {
			return Optional.empty();
		}

		Point3D calculatedPoint = calculatedPointOptional.get();

		logger.trace("Current position: X: {}, Y: {}", calculatedPoint.getX(), calculatedPoint.getY());

		Floor floor = anchorRepository.findByShortId(anchorId)
			.map(Anchor::getFloor)
			.orElse(null);
		if (floor == null) {
			return Optional.empty();
		}

		if (traceTags) {
			this.sendEventToTagTracer(tagId, floor);
		}

		Optional.ofNullable(previousCoorinates.get(tagId)).ifPresent((previousPoint) -> {
			calculatedPoint.setX((calculatedPoint.getX() + previousPoint.getPoint().getX()) / 2);
			calculatedPoint.setY((calculatedPoint.getY() + previousPoint.getPoint().getY()) / 2);
			calculatedPoint.setZ((calculatedPoint.getZ() + previousPoint.getPoint().getZ())/ 2);
		});
		Date currentDate = new Date();
		previousCoorinates.put(tagId, new PointAndTime(calculatedPoint, currentDate.getTime()));
		return Optional.of(new UwbCoordinatesDto(tagId, anchorId, floor.getId(), calculatedPoint, currentDate));
	}

	private Optional<Point3D> calculate3d(Set<Integer> connectedAnchors, Integer tagId) {
		int N = connectedAnchors.size();

		if (N < 4) {
			logger.trace(String.format("Not enough connected anchors to calculate position. Currently connected anchors: %s", connectedAnchors.size()));
			return Optional.empty();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());

		StateMatrix stateMatrix = getStateMatrix(connectedAnchors, tagId);

		logger.trace("State matrix: %s", stateMatrix);

		SimpleMatrix A = new SimpleMatrix(N, 3);
		SimpleMatrix b = new SimpleMatrix(N, 1);

		for (int taylorIter = 0; taylorIter < 10; ++taylorIter) {
			for (int i = 0; i < N; ++i) {
				SimpleMatrix delta = stateMatrix.anchorPositions.rows(i, i + 1)
					.minus(stateMatrix.tagPosition.transpose());
				double estimatedDistance = delta.normF();
				double distance = stateMatrix.measures.get(i);
				SimpleMatrix divided;
				if (estimatedDistance != 0) {
					divided = delta.divide(-estimatedDistance);
				} else {
					divided = delta.divide(-distance);
				}
				A.setRow(i, 0, divided.get(0), divided.get(1), divided.get(2));
				b.setRow(i, 0, distance - estimatedDistance);
			}

			SimpleMatrix aa = A.transpose().mult(A);
			SimpleMatrix ab = A.transpose().mult(b);
			SimpleMatrix p = (aa).solve(ab);

			stateMatrix.tagPosition = stateMatrix.tagPosition.plus(p);
			logger.trace("Tag position calculated matrix: %s", stateMatrix.tagPosition.toString());

			if (p.normF() < 10) {
				logger.trace("Less than 10 iteration was needed: %s", taylorIter);
				break;
			}
		}

		double x = stateMatrix.tagPosition.get(0);
		double y = stateMatrix.tagPosition.get(1);
		double z = stateMatrix.tagPosition.get(2);

		if (!isTagPositionGood(stateMatrix)) {
			logger.trace("Tag position calculated far too far: x = %s, y = %s, z = %s", x, y, z);
			return Optional.empty();
		}

		return Optional.of(new Point3D((int) Math.round(x), (int) Math.round(y), (int) Math.round(z)));
	}

	private StateMatrix getStateMatrix(Set<Integer> connectedAnchors, Integer tagId) {
		int N = connectedAnchors.size();
		SimpleMatrix anchorPositions = new SimpleMatrix(N, 3);
		SimpleMatrix measures = new SimpleMatrix(N, 1);
		SimpleMatrix tagPosition = new SimpleMatrix(3, 1);

		if (previousCoorinates.containsKey(tagId)) {
			Point3D tagPreviousCoordinates = previousCoorinates.get(tagId).point;
			tagPosition.setColumn(0, 0, tagPreviousCoordinates.getX(), tagPreviousCoordinates.getY(), tagPreviousCoordinates.getZ());
		} else {
			Optional<Integer> firstAnchorOptional = connectedAnchors.stream().findFirst();
			firstAnchorOptional.ifPresent((Integer firstAnchorShortId) -> {
				Anchor firstAnchor = anchorRepository.findByShortId(firstAnchorShortId).orElseThrow(EntityNotFoundException::new);
				tagPosition.setColumn(0, 0, firstAnchor.getX(), firstAnchor.getY(), firstAnchor.getZ());
			});
		}

		Integer[] anchors = connectedAnchors.toArray(new Integer[0]);
		for (int i = 0; i < N; ++i) {
			Integer currentAnchorShortId = anchors[i];
			Anchor currentAnchor = anchorRepository.findByShortId(currentAnchorShortId).orElseThrow(EntityNotFoundException::new);
			anchorPositions.setRow(i, 0, currentAnchor.getX(), currentAnchor.getY(), currentAnchor.getZ());
			measures.setRow(i, 0, getDistance(tagId, currentAnchorShortId));
		}

		return new StateMatrix(anchorPositions, measures, tagPosition);
	}

	private boolean isTagPositionGood(StateMatrix stateMatrix) {
		double maxDistance = stateMatrix.measures.elementMaxAbs();
		double maxPosition = stateMatrix.tagPosition.elementMaxAbs();
		boolean tooFar, badValue;
		tooFar = Math.abs(stateMatrix.tagPosition.get(0)) > stateMatrix.anchorPositions.cols(0, 1).elementMaxAbs() + maxDistance;
		tooFar |= Math.abs(stateMatrix.tagPosition.get(1)) > stateMatrix.anchorPositions.cols(1, 2).elementMaxAbs() + maxDistance;
		tooFar |= Math.abs(stateMatrix.tagPosition.get(2)) > stateMatrix.anchorPositions.cols(2, 3).elementMaxAbs() + maxDistance;
		badValue = Double.isInfinite(maxPosition) || Double.isNaN(maxPosition);
		return !(tooFar || badValue);
	}


	private Optional<Point3D> calculate2d(Set<Integer> connectedAnchors, Integer tagId) {
		if (connectedAnchors.size() < 3) {
			logger.trace(String.format("Not enough connected anchors to calculate position. Currently connected anchors: %s", connectedAnchors.size()));
			return Optional.empty();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());

		Set<Pair<AnchorDistance, AnchorDistance>> pairs = getAnchorDistancePairs(connectedAnchors, tagId);
		List<Point> intersectionPoints = new ArrayList<>();
		int validAnchorsCount = 0;
		for (Pair<AnchorDistance, AnchorDistance> pair : pairs) {
			Optional<Anchor> left = anchorRepository.findOptionalByShortIdAndPositionNotNull(pair.getLeft().getAnchorId());
			Optional<Anchor> right = anchorRepository.findOptionalByShortIdAndPositionNotNull(pair.getRight().getAnchorId());
			if (!left.isPresent() || !right.isPresent()) {
				continue;
			}

			validAnchorsCount++;
			intersectionPoints.addAll(IntersectionsCalculator.getIntersections(
				left.get(), calculatePitagoras(left.get(), pair.getLeft().getDistance()),
				right.get(), calculatePitagoras(right.get(), pair.getRight().getDistance())
			));
		}

		logger.trace("Anchor pairs: {}", pairs.size());

		if (validAnchorsCount < 3) {
			logger.trace(String.format("Not enough valid anchors to calculate position. Currently valid anchors: %s", validAnchorsCount));
			return Optional.empty();
		}

		List<Double> intersectionPointsDistance = IntersectionsCalculator.calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
		Double thres = calculateThres(intersectionPointsDistance, validAnchorsCount);

		logger.trace("Thres calculated: {}", thres);
		logger.trace("Intersection points found: {}", intersectionPoints.size());

		int x = 0, y = 0, j = 0;
		for (int ip = 0; ip < intersectionPoints.size(); ++ip) {
			if (intersectionPointsDistance.get(ip) <= thres) {
				x += intersectionPoints.get(ip).getX();
				y += intersectionPoints.get(ip).getY();
				++j;
			}
		}
		x /= j;
		y /= j;

		return Optional.of(new Point3D(x, y, 0));
	}

	private double calculatePitagoras(Anchor anchor, double distance) {
		int z = anchor.getZ();
		if (z > distance + 100) {
			logger.trace("Warning! Anchor shortId = {} height is higher than distance +/- 100 cm", anchor.getShortId());
		}
		double result = Math.pow(distance, 2) - Math.pow(z - TAG_Z, 2);
		return result < 0 ? 0 : Math.sqrt(result);
	}

	private void sendEventToTagTracer(Integer tagId, final Floor floor) {
		tagRepository.findOptionalByShortId(tagId).ifPresent((tag -> {
			tagTraceEvent.fire(new TagTraceDto(
				new TagDto(tag),
				new FloorDto(floor)
			));
		}));
	}

	private Double calculateThres(List<Double> intersectionPointsDistances, int connectedAnchorsCount) {
		List<Double> sortedIntersectionPointsDistance = new ArrayList<>(intersectionPointsDistances);
		Collections.sort(sortedIntersectionPointsDistance);
		Double thresBase = sortedIntersectionPointsDistance.get(connectedAnchorsCount - 1);
		Double thres = thresBase;
		for (int i = connectedAnchorsCount; i < intersectionPointsDistances.size(); i++) {
			if (sortedIntersectionPointsDistance.get(i) / thresBase - 1 < 0.10) {
				thres = sortedIntersectionPointsDistance.get(i);
			} else {
				break;
			}
		}
		return thres;
	}

	/**
	 * Choose tag id from two devices ids. Tags have id lower than 32767.
	 *
	 * @param firstDeviceId  id of the first device
	 * @param secondDeviceId id of the second device
	 * @return tag id if found otherwise null
	 */
	private Integer getTagId(int firstDeviceId, int secondDeviceId) {
		if (firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) {
			return firstDeviceId;
		} else if (secondDeviceId <= Short.MAX_VALUE && firstDeviceId > Short.MAX_VALUE) {
			return secondDeviceId;
		}
		return null;
	}

	/**
	 * Choose anchor id from two devices ids. Anchors have id higher than 32767.
	 *
	 * @param firstDeviceId  id of the first device
	 * @param secondDeviceId id of the second device
	 * @return anchor id if found otherwise null
	 */
	private Integer getAnchorId(int firstDeviceId, int secondDeviceId) {
		if (firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) {
			return secondDeviceId;
		} else if (secondDeviceId <= Short.MAX_VALUE && firstDeviceId > Short.MAX_VALUE) {
			return firstDeviceId;
		}
		return null;
	}

	private void setConnection(int tagId, int anchorId, double distance) {
		long now = new Date().getTime();
		if (measureStorage.containsKey(tagId)) {
			Map<Integer, List<Measure>> anchorsMeasures = measureStorage.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId);
				measures.add(new Measure(distance, now));
			} else {
				anchorsMeasures.put(anchorId, new LinkedList<>(Collections.singletonList(new Measure(distance, now))));
			}
		} else {
			Map<Integer, List<Measure>> anchorsMeasures = new LinkedHashMap<>();
			anchorsMeasures.put(anchorId, new LinkedList<>(Collections.singletonList(new Measure(distance, now))));
			measureStorage.put(tagId, anchorsMeasures);
		}
	}

	private void cleanOldData() {
		long now = new Date().getTime();
		measureStorage.entrySet()
			.removeIf(tagEntry -> tagEntry.getValue().entrySet()
				.removeIf(anchorEntry -> anchorEntry.getValue()
					.removeIf(measure -> new Date((now - OLD_DATA_IN_MILISECONDS)).after(new Date(measure.getTimestamp())))
				)
			);
	}

	private Set<Integer> getConnectedAnchors(Integer tagId) {
		this.cleanOldData();
		Set<Integer> connectedAnchors = new HashSet<>();
		if (measureStorage.containsKey(tagId)) {
			connectedAnchors.addAll(measureStorage.get(tagId).keySet());
		}
		return connectedAnchors;
	}

	private double getDistance(Integer tagId, Integer anchorId) {
		double meanDistance = 0d;
		if (measureStorage.containsKey(tagId)) {
			Map<Integer, List<Measure>> anchorsMeasures = measureStorage.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId);
				meanDistance = measures.stream().mapToDouble(Measure::getDistance).sum() / measures.size();
			}
		}
		return meanDistance;
	}

	private Set<Pair<AnchorDistance, AnchorDistance>> getAnchorDistancePairs(Set<Integer> connectedAnchors, Integer tagId) {
		Set<Pair<AnchorDistance, AnchorDistance>> pairs = new HashSet<>();
		Integer[] connectedAnchorsArray = connectedAnchors.toArray(new Integer[0]);
		for (int i = 0; i < connectedAnchors.size() - 1; i++) {
			for (int j = i + 1; j < connectedAnchors.size(); j++) {
				pairs.add(new ImmutablePair<>(
						new AnchorDistance(connectedAnchorsArray[i], getDistance(tagId, connectedAnchorsArray[i])),
						new AnchorDistance(connectedAnchorsArray[j], getDistance(tagId, connectedAnchorsArray[j]))
					)
				);
			}
		}
		return pairs;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private static class Measure {

		double distance;
		long timestamp;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class AnchorDistance {

		int anchorId;
		double distance;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class PointAndTime {

		private Point3D point;
		private long timestamp;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	private class StateMatrix {

		private SimpleMatrix anchorPositions;
		private SimpleMatrix measures;
		private SimpleMatrix tagPosition;
	}
}
