package pl.indoornavi.coordinatescalculator.algorithms;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.indoornavi.coordinatescalculator.models.AnchorDistance;
import pl.indoornavi.coordinatescalculator.models.Anchor;
import pl.indoornavi.coordinatescalculator.models.Point3D;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;
import pl.indoornavi.coordinatescalculator.shared.Storage;

import java.util.*;
import java.util.List;

import static pl.indoornavi.coordinatescalculator.shared.IntersectionsCalculator.*;

public class GeoN2d implements Algorithm {
	private final static int TAG_Z = 100;

	private final static int MAX_DIFFERENCE_BETWEEN_DISTANCE_AND_ANCHOR_HEIGHT = 100;

	private final static Logger logger = LoggerFactory.getLogger(GeoN2d.class);

	public GeoN2d(Storage storage, AnchorRepository anchorRepository) {
		this.storage = storage;
		this.anchorRepository = anchorRepository;
	}

	private final Storage storage;
	private final AnchorRepository anchorRepository;

	@Override
	public Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId) {
		if (connectedAnchors.size() < 3) {
			logger.trace("Not enough connected anchors to calculate position. Currently connected anchors: {}", connectedAnchors.size());
			return Optional.empty();
		}

		logger.trace("Connected anchors: {}", connectedAnchors.size());

		Set<Pair<AnchorDistance, AnchorDistance>> pairs = getAnchorDistancePairs(connectedAnchors, tagId);
		List<Point3D> intersectionPoints = new ArrayList<>();
		int validAnchorsCount = 0;
		for (Pair<AnchorDistance, AnchorDistance> pair : pairs) {
			Optional<Anchor> left = anchorRepository.findByShortIdAndPositionNotNull(pair.getLeft().getAnchorId());
			Optional<Anchor> right = anchorRepository.findByShortIdAndPositionNotNull(pair.getRight().getAnchorId());
			if (!left.isPresent() || !right.isPresent()) {
				continue;
			}

			validAnchorsCount++;
			intersectionPoints.addAll(getIntersections(
				left.get(), calculatePythagoras(left.get(), pair.getLeft().getDistance()),
				right.get(), calculatePythagoras(right.get(), pair.getRight().getDistance())
			));
		}

		logger.trace("Anchor pairs count: {}", pairs.size());

		if (validAnchorsCount < 3) {
			logger.trace("Not enough valid anchors to calculate position. Currently valid anchors: {}", validAnchorsCount);
			return Optional.empty();
		}

		List<Double> intersectionPointsDistance = calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
		Double thres = calculateThres(intersectionPointsDistance, validAnchorsCount);

		logger.trace("Thres calculated: {}", thres);
		logger.trace("Intersection points found: {}", intersectionPoints.size());

		int x = 0, y = 0, j = 0;
		for (int ip = 0, size = intersectionPoints.size(); ip < size; ++ip) {
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

	private Set<Pair<AnchorDistance, AnchorDistance>> getAnchorDistancePairs(List<Integer> connectedAnchors, Integer tagId) {
		Set<Pair<AnchorDistance, AnchorDistance>> pairs = new HashSet<>();
		Integer[] connectedAnchorsArray = connectedAnchors.toArray(new Integer[0]);
		long minTimestamp = storage.getTimeOfLastMeasure(tagId);
		for (int i = 0, size = connectedAnchors.size(); i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				pairs.add(new ImmutablePair<>(
						new AnchorDistance(
							connectedAnchorsArray[i],
							useInterpolation ?
								storage.getInterpolatedDistance(tagId, connectedAnchorsArray[i], minTimestamp) :
								storage.getDistance(tagId, connectedAnchorsArray[i])
						),
						new AnchorDistance(
							connectedAnchorsArray[j],
							useInterpolation ?
								storage.getInterpolatedDistance(tagId, connectedAnchorsArray[j], minTimestamp) :
								storage.getDistance(tagId, connectedAnchorsArray[j])
						)
					)
				);
			}
		}
		return pairs;
	}

	private double calculatePythagoras(Anchor anchor, double distance) {
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
}
