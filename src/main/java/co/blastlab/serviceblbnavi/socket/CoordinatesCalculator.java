package co.blastlab.serviceblbnavi.socket;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CoordinatesCalculator {
	// <tagId, anchorId, measure>
	private Table<Integer, Integer, Measure> measureTable = HashBasedTable.create();

	private FakeDb db = new FakeDb();

	public Optional<Point> calculateTagPosition(int firstDeviceId, int secondDeviceId, double distance) {
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
		pairs.forEach(pair -> {
			intersectionPoints.addAll(IntersectionsCalculator.getIntersections(
				db.findBy(pair.getLeft().getAnchorId()), pair.getLeft().getDistance(),
				db.findBy(pair.getRight().getAnchorId()), pair.getRight().getDistance()
			));
		});

		List<Double> intersectionPointsDistance = IntersectionsCalculator.calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
		List<Double> sortedIntersectionPointsDistance = new ArrayList<>();
		sortedIntersectionPointsDistance.addAll(intersectionPointsDistance);
		Collections.sort(sortedIntersectionPointsDistance);
		Double thres = sortedIntersectionPointsDistance.get(connectedAnchors.size() - 1);

		int x = 0;
		int y = 0;
		int j = 0;
		for(int ip = 0; ip < intersectionPoints.size(); ++ip)
		{
			if (intersectionPointsDistance.get(ip) > thres)
				continue;
			else
			{
				x += intersectionPoints.get(ip).getX();
				y += intersectionPoints.get(ip).getY();
				++j;
			}
		}
		x /= j;
		y /= j;

		clearConnectedAnchors(tagId);
		return Optional.of(new Point(x, y));
	}

	protected void cleanMeasureTable() {
		// TODO: remove old measures
	}

	/**
	 * Choose tag id from two devices ids. Tags have id lower than 32767.
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
		measureTable.put(tagId, anchorId, new Measure(distance, new Date().getTime()));
	}

	private Set<Integer> getConnectedAnchors(Integer tagId) {
		Set<Integer> connectedAnchors = new HashSet<>();
		if (measureTable.containsRow(tagId)) {
			Map<Integer, Measure> row = measureTable.row(tagId);
			connectedAnchors = row.keySet();
		}
		return connectedAnchors;
	}

	private void clearConnectedAnchors(Integer tagId) {
		synchronized (measureTable) {
			if (measureTable.containsRow(tagId)) {
				measureTable.row(tagId).clear();
			}
		}
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

	private Double getDistance(Integer tagId, Integer anchorId) {
		return measureTable.get(tagId, anchorId).getDistance();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private class Measure {
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
}
