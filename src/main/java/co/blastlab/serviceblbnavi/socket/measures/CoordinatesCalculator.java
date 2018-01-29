package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.*;

@Singleton
public class CoordinatesCalculator {

	// <tagId, anchorShortId, measure>
	private Table<Integer, Integer, List<Measure>> measureTable = HashBasedTable.create();

	// 30 minutes
	private final static long OLD_DATA_IN_MILISECONDS = 1_800_000;

	private Map<Integer, PointAndTime> previousCoorinates = new HashMap<>();

	@Inject
	private AnchorRepository anchorRepository;

	public Optional<CoordinatesDto> calculateTagPosition(int firstDeviceId, int secondDeviceId, int distance) {
		Integer tagId = getTagId(firstDeviceId, secondDeviceId);
		Integer anchorId = getAnchorId(firstDeviceId, secondDeviceId);
		if (tagId == null || anchorId == null) {
			return Optional.empty();
		}

		setConnection(tagId, anchorId, distance);

		Set<Integer> connectedAnchors = getConnectedAnchors(tagId);
		if (connectedAnchors.size() < 3) {
			return Optional.empty();
		}

		Set<Pair<AnchorDistance, AnchorDistance>> pairs = getAnchorDistancePairs(connectedAnchors, tagId);
		List<Point> intersectionPoints = new ArrayList<>();
		for (Pair<AnchorDistance, AnchorDistance> pair : pairs) {
			Optional<Anchor> left = anchorRepository.findByShortId(pair.getLeft().getAnchorId());
			Optional<Anchor> right = anchorRepository.findByShortId(pair.getRight().getAnchorId());
			if (!left.isPresent() || !right.isPresent()) {
				return Optional.empty();
			}
			intersectionPoints.addAll(IntersectionsCalculator.getIntersections(
				left.get(), pair.getLeft().getDistance(),
				right.get(), pair.getRight().getDistance()
			));
		}


		List<Double> intersectionPointsDistance = IntersectionsCalculator.calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
		Double thres = calculateThres(intersectionPointsDistance, connectedAnchors.size());
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
		Optional<PointAndTime> previousPoint = Optional.ofNullable(previousCoorinates.get(tagId));
		Long floorId = null;
		Optional<Anchor> anchor = anchorRepository.findByShortId(anchorId);
		if (anchor.isPresent()) {
			floorId = anchor.get().getFloor() != null ? anchor.get().getFloor().getId() : null;
		}
		Date currentDate = new Date();
		if (previousPoint.isPresent()) {
			x = (x + previousPoint.get().getPoint().getX()) / 2;
			y = (y + previousPoint.get().getPoint().getY()) / 2;
			Point newPoint = new Point(x, y);
			previousCoorinates.put(tagId, new PointAndTime(newPoint, currentDate.getTime()));
			return Optional.of(new CoordinatesDto(tagId, anchorId, floorId, newPoint, currentDate));
		}
		Point currentPoint = new Point(x, y);
		previousCoorinates.put(tagId, new PointAndTime(currentPoint, currentDate.getTime()));
		return Optional.of(new CoordinatesDto(tagId, anchorId, floorId, currentPoint, currentDate));
	}

	private Double calculateThres(List<Double> intersectionPointsDistances, int connectedAnchorsCount) {
		List<Double> sortedIntersectionPointsDistance = new ArrayList<>();
		sortedIntersectionPointsDistance.addAll(intersectionPointsDistances);
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
	 * Remove old data, older than OLD_DATA_IN_MILISECONDS
	 */
	public void cleanTables() {
		long now = new Date().getTime();

		Iterator<Table.Cell<Integer, Integer, List<Measure>>> measureIterator = measureTable.cellSet().iterator();

		measureIterator.forEachRemaining(cell -> {
			cell.getValue().removeIf(measure -> new Date((now - OLD_DATA_IN_MILISECONDS)).after(new Date(measure.getTimestamp())));
		});

		Iterator<Map.Entry<Integer, PointAndTime>> pointsIterator = previousCoorinates.entrySet().iterator();

		pointsIterator.forEachRemaining(point -> {
			if (new Date((now - OLD_DATA_IN_MILISECONDS)).after(new Date(point.getValue().getTimestamp()))) {
				pointsIterator.remove();
			}
		});
	}

	/**
	 * Choose tag id from two devices ids. Tags have id lower than 32767.
	 *
	 * @param firstDeviceId id of the first device
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
	 * @param firstDeviceId id of the first device
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
		if (measureTable.contains(tagId, anchorId)) {
			Measure measure = new Measure(distance, new Date().getTime());
			if (measureTable.get(tagId, anchorId).size() >= 5) {
				measureTable.get(tagId, anchorId).remove(0);
			}
			measureTable.get(tagId, anchorId).add(measure);
		} else {
			measureTable.put(tagId, anchorId, new ArrayList<>(Collections.singletonList(new Measure(distance, new Date().getTime()))));
		}
	}

	private Set<Integer> getConnectedAnchors(Integer tagId) {
		Set<Integer> connectedAnchors = new HashSet<>();
		if (measureTable.containsRow(tagId)) {
			Map<Integer, List<Measure>> row = measureTable.row(tagId);
			connectedAnchors = row.keySet();
		}
		return connectedAnchors;
	}

	private Double getDistance(Integer tagId, Integer anchorId) {
		return measureTable.get(tagId, anchorId).stream().mapToDouble(Measure::getDistance).sum() / measureTable.get(tagId, anchorId).size();
	}

	private Set<Pair<AnchorDistance, AnchorDistance>> getAnchorDistancePairs(Set<Integer> connectedAnchors, Integer tagId) {
		Set<Pair<AnchorDistance, AnchorDistance>> pairs = new HashSet<>();
		Integer[] connectedAnchorsArray = connectedAnchors.toArray(new Integer[connectedAnchors.size()]);
		for (int i = 0; i < connectedAnchors.size() - 1; i++) {
			pairs.add(new ImmutablePair<>(
					new AnchorDistance(connectedAnchorsArray[i], getDistance(tagId, connectedAnchorsArray[i])),
					new AnchorDistance(connectedAnchorsArray[i + 1], getDistance(tagId, connectedAnchorsArray[i + 1]))
				)
			);
		}
		Integer firstAnchorId = connectedAnchorsArray[0];
		Integer lastAnchorId = connectedAnchorsArray[connectedAnchors.size() - 1];
		pairs.add(new ImmutablePair<>(
				new AnchorDistance(firstAnchorId, getDistance(tagId, firstAnchorId)),
				new AnchorDistance(lastAnchorId, getDistance(tagId, lastAnchorId))
			)
		);
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

		private Point point;
		private long timestamp;
	}
}
