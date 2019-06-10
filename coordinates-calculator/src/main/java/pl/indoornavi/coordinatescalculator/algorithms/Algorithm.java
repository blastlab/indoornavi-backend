package pl.indoornavi.coordinatescalculator.algorithms;

import pl.indoornavi.coordinatescalculator.models.Point3D;

import java.util.List;
import java.util.Optional;

public interface Algorithm {
	boolean useInterpolation = true;
	Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId);
}
