package co.blastlab.serviceblbnavi.socket.utils;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.floor.Point;
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
	// <tagId, anchorId, measure>
	private Table<Integer, Integer, Measure> measureTable = HashBasedTable.create();

	// 30 minutes
	private final static long OLD_MEASURE_IN_MILISECONDS = 1_800_000;

	@Inject
	private AnchorRepository anchorRepository;

	public Optional<CoordinatesDto> calculateTagPosition(int firstDeviceId, int secondDeviceId, double distance) {
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
				anchorRepository.findByShortId(pair.getLeft().getAnchorId()), pair.getLeft().getDistance(),
				anchorRepository.findByShortId(pair.getRight().getAnchorId()), pair.getRight().getDistance()
			));
		});

		List<Double> intersectionPointsDistance = IntersectionsCalculator.calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
		List<Double> sortedIntersectionPointsDistance = new ArrayList<>();
		sortedIntersectionPointsDistance.addAll(intersectionPointsDistance);
		Collections.sort(sortedIntersectionPointsDistance);
		Double thres = sortedIntersectionPointsDistance.get(connectedAnchors.size() - 1);

		int x = 0, y = 0, j = 0;
		for (int ip = 0; ip < intersectionPoints.size(); ++ip)
		{
			if (intersectionPointsDistance.get(ip) <= thres) {
				x += intersectionPoints.get(ip).getX();
				y += intersectionPoints.get(ip).getY();
				++j;
			}
		}
		// TODO: uśrednienie na podstawie poprzedniej wartości taga x,y
		x /= j;
		y /= j;

		clearConnectedAnchors(tagId);
		return Optional.of(new CoordinatesDto(tagId, new Point(x, y)));
	}

	/**
	 * Remove from measure table measures older than 30 minutes
	 */
	public void cleanMeasureTable() {
		long now = new Date().getTime();

		Iterator<Table.Cell<Integer, Integer, Measure>> iterator = measureTable.cellSet().iterator();

		iterator.forEachRemaining(cell -> {
			if (now - OLD_MEASURE_IN_MILISECONDS < cell.getValue().getTimestamp()) {
				iterator.remove();
			}
		});
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

	// TODO: uśrednienie dystansów
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
}
