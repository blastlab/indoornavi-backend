package pl.indoornavi.coordinatescalculator.algorithms;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import pl.indoornavi.coordinatescalculator.models.Anchor;
import pl.indoornavi.coordinatescalculator.models.Point3D;
import pl.indoornavi.coordinatescalculator.models.PointAndTime;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;
import pl.indoornavi.coordinatescalculator.shared.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.indoornavi.coordinatescalculator.shared.IntersectionsCalculator.calculateThres;
import static pl.indoornavi.coordinatescalculator.shared.IntersectionsCalculator.getIntersections3d;

public class GeoN3d extends Algorithm3d implements Algorithm {
	@Autowired
	public GeoN3d(AnchorRepository anchorRepository, Storage storage) {
		this.storage = storage;
		this.anchorRepository = anchorRepository;
	}
	private final AnchorRepository anchorRepository;
	private final Storage storage;

	@Override
	protected AnchorRepository getAnchorRepository() {
		return anchorRepository;
	}

	@Override
	public Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId) {
		List<Anchor> anchors;
		try {
			anchors = getAnchors(connectedAnchors);
		} catch (NotEnoughAnchors notEnoughAnchors) {
			return Optional.empty();
		}

		List<Pair<SimpleMatrix, Float>> pairs = new ArrayList<>();

		long minTimestamp = storage.getTimeOfLastMeasure(tagId);

		for (int i = 1; i < anchors.size(); i++) {
			Anchor firstAnchor = anchors.get(i);
			double firstDistance = useInterpolation ?
				storage.getInterpolatedDistance(tagId, firstAnchor.getShortId(), minTimestamp) :
				storage.getDistance(tagId, firstAnchor.getShortId());

			for (int j = 0; j < i; j++) {
				Anchor secondAnchor = anchors.get(j);
				double secondDistance = useInterpolation ?
					storage.getInterpolatedDistance(tagId, secondAnchor.getShortId(), minTimestamp) :
					storage.getDistance(tagId, secondAnchor.getShortId());

				for (int k = 0; k < j; k++) {
					Anchor thirdAnchor = anchors.get(k);
					double thirdDistance = useInterpolation ?
						storage.getInterpolatedDistance(tagId, thirdAnchor.getShortId(), minTimestamp) :
						storage.getDistance(tagId, thirdAnchor.getShortId());
					pairs.addAll(getIntersections3d(firstAnchor, firstDistance, secondAnchor, secondDistance, thirdAnchor, thirdDistance));
				}
			}
		}

		PointAndTime previousTagPosition = storage.getPreviousCoordinates().get(tagId);
		if (previousTagPosition != null) {
			pairs.add(new ImmutablePair<>(
				createPositionMatrix(previousTagPosition.getPoint()),
				2f
			));
		}

		List<Double> distances = new ArrayList<>(pairs.size());
		for (int i = 0; i < pairs.size(); i++) {
			distances.add(0d);
			for (Pair<SimpleMatrix, Float> pair : pairs) {
				Double newDistance = distances.get(i) + (pairs.get(i).getKey().minus(pair.getKey())).normF();
				distances.set(i, newDistance);
			}
		}

		Double thres = calculateThres(distances, anchors.size());

		float weight = 0;
		SimpleMatrix sum = new SimpleMatrix(new double[][]{
			new double[]{0, 0, 0}
		}).transpose();
		for (int i = 0; i < pairs.size(); i++) {
			if (distances.get(i) <= thres) {
				sum = sum.plus(pairs.get(i).getKey());
				weight += 1;
			}
		}

		SimpleMatrix tagPosition = sum.scale(1 / weight);

		return Optional.of(new Point3D(
			(int) Math.round(tagPosition.get(0)),
			(int) Math.round(tagPosition.get(1)),
			(int) Math.round(tagPosition.get(2)))
		);
	}

	private SimpleMatrix createPositionMatrix(Point3D point) {
		return new SimpleMatrix(
			new double[][]{
				new double[]{point.getX()},
				new double[]{point.getY()},
				new double[]{point.getZ()}
			}
		);
	}

}
