package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.Point;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.tagTracer.TagTraceDto;
import co.blastlab.indoornavi.utils.Logger;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.ejml.simple.SimpleMatrix;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CoordinatesCalculator {

	// tag short id, anchor short id, measure list
	private Map<Integer, Map<Integer, PolyMeasure>> measureStorage = new LinkedHashMap<>();

	// 10 seconds
	private final static long OLD_DATA_IN_MILISECONDS = 10_000;

	private final static int TAG_Z = 100;

	private final static int MAX_DIFFERENCE_BETWEEN_DISTANCE_AND_ANCHOR_HEIGHT = 100;

	private Map<Integer, PointAndTime> previousCoorinates = new HashMap<>();

	@Inject
	private Logger logger;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	private Map<Integer, Integer> tagToSinkMapping = new HashMap<>();

	private boolean traceTags;

	@Inject
	private Event<TagTraceDto> tagTraceEvent;

	public void startTracingTags() {
		this.traceTags = true;
	}

	public void stopTracingTags() {
		this.traceTags = false;
	}

	public Optional<Integer> findSinkForTag(Integer tagShortId) {
		return tagToSinkMapping.containsKey(tagShortId) ? Optional.of(tagToSinkMapping.get(tagShortId)) : Optional.empty();
	}

	public Optional<UwbCoordinatesDto> calculateTagPosition(int firstDeviceId, int secondDeviceId, int distance, AlgorithmType algorithmType) {
		logger.trace("Measure storage tags: {}", measureStorage.keySet().size());

		Integer tagId = getTagId(firstDeviceId, secondDeviceId);
		Integer anchorId = getAnchorId(firstDeviceId, secondDeviceId);
		if (tagId == null || anchorId == null) {
			logger.trace("One of the devices' ids is out of range. Ids are: {}, {} and range is (1, {})", firstDeviceId, secondDeviceId, Short.MAX_VALUE);
			return Optional.empty();
		}

		if (isSink(anchorId)) {
			tagToSinkMapping.put(tagId, anchorId);
		}

		setConnection(tagId, anchorId, distance);

		Set<Integer> connectedAnchors = getConnectedAnchors(tagId);

		Optional<Point3D> calculatedPointOptional = Optional.empty();

		switch (algorithmType) {
			case TAYLOR:
				calculatedPointOptional = calculateTaylor(connectedAnchors, tagId);
				break;
			case GEO_N_2D:
				calculatedPointOptional = calculate2d(connectedAnchors, tagId);
				break;
			case GEO_N_3D:
				calculatedPointOptional = calculate3d(connectedAnchors, tagId);
				break;
			default:
				calculatedPointOptional = calculate2d(connectedAnchors, tagId);
		}

		if (!calculatedPointOptional.isPresent()) {
			return Optional.empty();
		}

		Point3D calculatedPoint = calculatedPointOptional.get();

		logger.trace("Current position: X: {}, Y: {}, Z: {}", calculatedPoint.getX(), calculatedPoint.getY(), calculatedPoint.getZ());

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
			calculatedPoint.setZ((calculatedPoint.getZ() + previousPoint.getPoint().getZ()) / 2);
		});
		Date currentDate = new Date();
		previousCoorinates.put(tagId, new PointAndTime(calculatedPoint, currentDate.getTime()));
		return Optional.of(new UwbCoordinatesDto(tagId, anchorId, floor.getId(), calculatedPoint, currentDate));
	}

	private boolean isSink(Integer anchorId) {
		Optional<Anchor> anchorOptional = anchorRepository.findByShortId(anchorId);
		return anchorOptional.filter(anchor -> anchor instanceof Sink).isPresent();
	}

	private Optional<Point3D> calculateTaylor(Set<Integer> connectedAnchors, Integer tagId) {
		int N = connectedAnchors.size();

		if (N < 4) {
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			return Optional.empty();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());

		List<Anchor> anchors = new ArrayList<>();
		for (Integer connectedAnchorShortId : connectedAnchors) {
			Optional<Anchor> anchorOptional = anchorRepository.findByShortId(connectedAnchorShortId);
			anchorOptional.ifPresent(anchors::add);
		}

		if (anchors.size() < 4) {
			logger.trace("Not enough connected and in database anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			return Optional.empty();
		}

		StateMatrix stateMatrix = getStateMatrix(anchors, tagId);

//		logger.trace("State matrix: {}", stateMatrix);

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
//			logger.trace("Tag position calculated matrix: {}", stateMatrix.tagPosition.toString());

			if (p.normF() < 10) {
				logger.trace("Less than 10 iteration was needed: {}", taylorIter);
				break;
			}
		}

		double x = stateMatrix.tagPosition.get(0);
		double y = stateMatrix.tagPosition.get(1);
		double z = stateMatrix.tagPosition.get(2) < 0 ? 0 : stateMatrix.tagPosition.get(2);

		double res = 0;
		double max = 0;
		Anchor maxA = null;
		for (int i = 0; i < anchors.size(); i++) {
			Anchor anchor = anchors.get(i);
			double _x = Math.pow(anchor.getX() - x, 2);
			double _y = Math.pow(anchor.getY() - y, 2);
			double _z = Math.pow(anchor.getZ() - z, 2);
			double curr = Math.abs(Math.sqrt(_x + _y + _z) - stateMatrix.measures.get(i));
			if (curr > max) {
				max = curr;
				maxA = anchor;
			}
			res += curr;
		}

		if (!isTagPositionGood(stateMatrix)) {
			logger.trace("Tag position calculated far too far: x = {}, y = {}, z = {}", x, y, z);
			return Optional.empty();
		}

		logger.trace("Tag position calculated: x = {}, y = {}, z = {}, res = {}, max = {} from = {}"
			, (int) Math.round(x), (int) Math.round(y), (int) Math.round(z), res, max, maxA.getShortId());

		return Optional.of(new Point3D((int) Math.round(x), (int) Math.round(y), (int) Math.round(z)));
	}

	private StateMatrix getStateMatrix(List<Anchor> connectedAnchors, Integer tagId) {
		int N = connectedAnchors.size();
		SimpleMatrix anchorPositions = new SimpleMatrix(N, 3);
		SimpleMatrix measures = new SimpleMatrix(N, 1);
		SimpleMatrix tagPosition = new SimpleMatrix(3, 1);

		if (previousCoorinates.containsKey(tagId)) {
			Point3D tagPreviousCoordinates = previousCoorinates.get(tagId).point;
			tagPosition.setColumn(0, 0, tagPreviousCoordinates.getX(), tagPreviousCoordinates.getY(), tagPreviousCoordinates.getZ());
		} else {
			connectedAnchors.stream().findFirst().ifPresent((Anchor firstAnchor) -> {
				tagPosition.setColumn(0, 0, firstAnchor.getX(), firstAnchor.getY(), firstAnchor.getZ());
			});
		}

		Anchor[] anchors = connectedAnchors.toArray(new Anchor[0]);
		for (int i = 0; i < N; ++i) {
			Anchor currentAnchor = anchors[i];
			anchorPositions.setRow(i, 0, currentAnchor.getX(), currentAnchor.getY(), currentAnchor.getZ());
			measures.setRow(i, 0, getDistance(tagId, currentAnchor.getShortId()));
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
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
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
			logger.trace("Not enough valid anchors to calculate position. Currently valid anchors: {}", validAnchorsCount);
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

	private Optional<Point3D> calculate3d(List<Integer> connectedAnchors, Integer tagId) {
		List<Pair<SimpleMatrix, Double>> pairs = new ArrayList<>();

		// get time of last measure
		long minTimestamp = new Date().getTime();
		for (Map.Entry<Integer, PolyMeasure> entry : measureStorage.get(tagId).entrySet()) {
			List<Measure> measures = entry.getValue().getMeasures();
			Optional<Measure> maxTimestampMeasure = measures.stream().max(Comparator.comparing(Measure::getTimestamp));
			if (maxTimestampMeasure.isPresent()) {
				minTimestamp = Math.min(minTimestamp, maxTimestampMeasure.get().getTimestamp());
			}
		}

		for (int i = 1; i < connectedAnchors.size(); ++i) {
			Integer anchorId = connectedAnchors.get(i);
			float dist1 = measures[i].GetRange(timestamp);
			Device_t anc1 = db.GetDevice(anc1_did);

			for (int indo = 0; indo < i; ++indo) {
				ushort anc2_did = connectedAnchorsDid[indo];
				Device_t anc2 = db.GetDevice(anc2_did);
				float dist2 = measures[indo].GetRange(timestamp);

				if (full3D) {
					for (int indu = 0; indu < indo; ++indu) {
						ushort anc3_did = connectedAnchorsDid[indu];
						Device_t anc3 = db.GetDevice(anc3_did);
						float dist3 = measures[indu].GetRange(timestamp);
						GetIntersections3d(anc1, dist1, anc2, dist2, anc3, dist3, ref ip);
					}
				} else {
					GetIntersections(anc1, dist1, anc2, dist2, ref ip);
				}
			}
		}
	}

	private double getInterpolatedDistance(Integer tagId, Integer anchorId, long timestamp) {
		List<Measure> measures = measureStorage.get(tagId).get(anchorId).getMeasures();
		if (measures.size() < 2 || measures.get(measures.size() - 1).getTimestamp() == timestamp) {
			return measures.get(measures.size() - 1).getDistance();
		}
		List<Measure> sorted = measures.stream().filter(measure -> measure.getTimestamp() < timestamp).sorted(Comparator.comparing(Measure::getTimestamp)).collect(Collectors.toList());
		Measure first = sorted.get(sorted.size() - 1);
		Measure last = measures.get(measures.size() - 1);
	}

	private double calculatePitagoras(Anchor anchor, double distance) {
		int z = anchor.getZ();
		checkAnchorIsInProperHeight(anchor, z, distance);
		double result = Math.pow(distance, 2) - Math.pow(z - TAG_Z, 2);
		return result < 0 ? 0 : Math.sqrt(result);
	}

	private void checkAnchorIsInProperHeight(Anchor anchor, int z, double distance) {
		if (z > distance + MAX_DIFFERENCE_BETWEEN_DISTANCE_AND_ANCHOR_HEIGHT) {
			logger.trace("Warning! Anchor shortId = {} height is higher than distance +100cm", anchor.getShortId());
		}
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
			Map<Integer, PolyMeasure> anchorsMeasures = measureStorage.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
				anchorsMeasures.get(anchorId).setPoly(calculatePoly(measures, now));
				measures.add(new Measure(distance, now));
			} else {
				anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}));
			}
		} else {
			Map<Integer, PolyMeasure> anchorsMeasures = new LinkedHashMap<>();
			anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}));
			measureStorage.put(tagId, anchorsMeasures);
		}

	}

	private double[] calculatePoly(List<Measure> measures, long now) {
		double T = measures.get(measures.size() - 1).getTimestamp() - measures.get(0).getTimestamp();
		List<WeightedObservedPoint> points = new ArrayList<>();
		double weightMin = 0.3;
		for (Measure measure : measures) {
			double x = measure.getTimestamp() - now;
			double y = measure.getDistance();
			double weight = (1 - weightMin) * (measure.getTimestamp() - measures.get(0).getTimestamp()) / T + weightMin;
			points.add(new WeightedObservedPoint(weight, x, y));
		}

		PolynomialCurveFitter polyFitter = PolynomialCurveFitter.create(2);
		return polyFitter.fit(points);
	}

	private void cleanOldData() {
		long now = new Date().getTime();
		measureStorage.entrySet()
			.removeIf(tagEntry -> tagEntry.getValue().entrySet()
				.removeIf(anchorEntry -> anchorEntry.getValue().getMeasures()
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
			Map<Integer, PolyMeasure> anchorsMeasures = measureStorage.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
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

	@Getter
	@Setter
	@AllArgsConstructor
	private static class PolyMeasure {
		private List<Measure> measures = new ArrayList<>();
		private double[] poly;
	}
}
