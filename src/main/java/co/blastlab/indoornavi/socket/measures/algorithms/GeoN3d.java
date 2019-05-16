package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.socket.measures.Point3D;
import co.blastlab.indoornavi.socket.measures.model.PointAndTime;
import co.blastlab.indoornavi.socket.measures.Storage;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.*;

import static co.blastlab.indoornavi.socket.measures.IntersectionsCalculator.calculateThres;
import static co.blastlab.indoornavi.socket.measures.IntersectionsCalculator.getIntersections3d;

public class GeoN3d extends Algorithm3d implements Algorithm {
	@Inject
	private Storage storage;

	@Override
	public Optional<Point3D> calculate(String sessionId, List<Integer> connectedAnchors, Integer tagId) {
		List<Anchor> anchors;
		try {
			anchors = getAnchors(sessionId, connectedAnchors);
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
