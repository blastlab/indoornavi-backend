package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.socket.measures.Point3D;

import javax.websocket.Session;
import java.util.List;
import java.util.Optional;

public interface Algorithm {
	boolean useInterpolation = true;
	Optional<Point3D> calculate(String sessionId, List<Integer> connectedAnchors, Integer tagId);
}
