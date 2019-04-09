package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.socket.measures.Point3D;

import java.util.List;
import java.util.Optional;

public interface Algorithm {
	boolean useInterpolation = true;
	Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId);
}
