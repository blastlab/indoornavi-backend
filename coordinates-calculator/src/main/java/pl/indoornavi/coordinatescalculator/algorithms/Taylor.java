package pl.indoornavi.coordinatescalculator.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ejml.simple.SimpleMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import pl.indoornavi.coordinatescalculator.models.AnchorDto;
import pl.indoornavi.coordinatescalculator.models.Point3D;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;
import pl.indoornavi.coordinatescalculator.shared.Storage;

import java.util.List;
import java.util.Optional;

public class Taylor extends Algorithm3d implements Algorithm {
	@Autowired
	public Taylor(AnchorRepository anchorRepository, Storage storage) {
		this.anchorRepository = anchorRepository;
		this.storage = storage;
	}
	private final AnchorRepository anchorRepository;
	private final Storage storage;

	@Override
	protected AnchorRepository getAnchorRepository() {
		return anchorRepository;
	}

	@Override
	public Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId) {
		List<AnchorDto> anchors;
		try {
			anchors = getAnchors(connectedAnchors);
		} catch (Algorithm3d.NotEnoughAnchors notEnoughAnchors) {
			return Optional.empty();
		}

		int N = connectedAnchors.size();

		Taylor.StateMatrix stateMatrix = getStateMatrix(anchors, tagId);

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

			if (p.normF() < 10) {
				break;
			}
		}

		double x = stateMatrix.tagPosition.get(0);
		double y = stateMatrix.tagPosition.get(1);
		double z = stateMatrix.tagPosition.get(2) < 0 ? 0 : stateMatrix.tagPosition.get(2);

		double max = 0;
		for (int i = 0; i < anchors.size(); i++) {
			AnchorDto anchor = anchors.get(i);
			double _x = Math.pow(anchor.getX() - x, 2);
			double _y = Math.pow(anchor.getY() - y, 2);
			double _z = Math.pow(anchor.getZ() - z, 2);
			double curr = Math.abs(Math.sqrt(_x + _y + _z) - stateMatrix.measures.get(i));
			if (curr > max) {
				max = curr;
			}
		}

		if (!isTagPositionGood(stateMatrix)) {
			return Optional.empty();
		}

		return Optional.of(new Point3D((int) Math.round(x), (int) Math.round(y), (int) Math.round(z)));
	}

	private StateMatrix getStateMatrix(List<AnchorDto> connectedAnchors, Integer tagId) {
		int N = connectedAnchors.size();
		SimpleMatrix anchorPositions = new SimpleMatrix(N, 3);
		SimpleMatrix measures = new SimpleMatrix(N, 1);
		SimpleMatrix tagPosition = new SimpleMatrix(3, 1);

		if (storage.getPreviousCoordinates().containsKey(tagId)) {
			Point3D tagPreviousCoordinates = storage.getPreviousCoordinates().get(tagId).getPoint();
			tagPosition.setColumn(0, 0, tagPreviousCoordinates.getX(), tagPreviousCoordinates.getY(), tagPreviousCoordinates.getZ());
		} else {
			connectedAnchors.stream().findFirst().ifPresent((AnchorDto firstAnchor) -> {
				tagPosition.setColumn(0, 0, firstAnchor.getX(), firstAnchor.getY(), firstAnchor.getZ());
			});
		}

		AnchorDto[] anchors = connectedAnchors.toArray(new AnchorDto[0]);
		for (int i = 0; i < N; ++i) {
			AnchorDto currentAnchor = anchors[i];
			anchorPositions.setRow(i, 0, currentAnchor.getX(), currentAnchor.getY(), currentAnchor.getZ());
			measures.setRow(i, 0, useInterpolation ?
				storage.getInterpolatedDistance(tagId, currentAnchor.getShortId(), storage.getTimeOfLastMeasure(tagId)) :
				storage.getDistance(tagId, currentAnchor.getShortId())
			);
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
