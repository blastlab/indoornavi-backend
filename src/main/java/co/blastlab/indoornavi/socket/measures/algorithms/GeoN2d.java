package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.dto.Point;
import co.blastlab.indoornavi.socket.measures.Point3D;
import co.blastlab.indoornavi.socket.measures.Storage;
import co.blastlab.indoornavi.socket.measures.model.AnchorDistance;
import co.blastlab.indoornavi.utils.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.util.*;

import static co.blastlab.indoornavi.socket.measures.IntersectionsCalculator.*;

@UseGeoN2d
public class GeoN2d implements Algorithm {
	private final static int TAG_Z = 100;

	private final static int MAX_DIFFERENCE_BETWEEN_DISTANCE_AND_ANCHOR_HEIGHT = 100;

	@Inject
	private Storage storage;
	@Inject
	private Logger logger;
	@Inject
	private AnchorRepository anchorRepository;

	@Override
	public Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId) {
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
			intersectionPoints.addAll(getIntersections(
				left.get(), calculatePitagoras(left.get(), pair.getLeft().getDistance()),
				right.get(), calculatePitagoras(right.get(), pair.getRight().getDistance())
			));
		}

		logger.trace("Anchor pairs: {}", pairs.size());

		if (validAnchorsCount < 3) {
			logger.trace("Not enough valid anchors to calculate position. Currently valid anchors: {}", validAnchorsCount);
			return Optional.empty();
		}

		List<Double> intersectionPointsDistance = calculateSumDistanceBetweenIntersectionPoints(intersectionPoints);
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

	private Set<Pair<AnchorDistance, AnchorDistance>> getAnchorDistancePairs(List<Integer> connectedAnchors, Integer tagId) {
		Set<Pair<AnchorDistance, AnchorDistance>> pairs = new HashSet<>();
		Integer[] connectedAnchorsArray = connectedAnchors.toArray(new Integer[0]);
		long minTimestamp = storage.getTimeOfLastMeasure(tagId);
		for (int i = 0; i < connectedAnchors.size() - 1; i++) {
			for (int j = i + 1; j < connectedAnchors.size(); j++) {
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
}
