package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.socket.measures.Point3D;
import co.blastlab.indoornavi.socket.measures.model.Measure;
import co.blastlab.indoornavi.socket.measures.model.PolyMeasure;
import co.blastlab.indoornavi.socket.measures.Storage;
import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;

import javax.inject.Inject;
import java.util.*;

public class GeoN3d implements Algorithm {
	@Inject
	private Storage storage;

	@Override
	public Optional<Point3D> calculate(List<Integer> connectedAnchors, Integer tagId) {
		List<Pair<SimpleMatrix, Double>> pairs = new ArrayList<>();

		// get time of last measure
		long minTimestamp = new Date().getTime();
		for (Map.Entry<Integer, PolyMeasure> entry : storage.getMeasures().get(tagId).entrySet()) {
			List<Measure> measures = entry.getValue().getMeasures();
			Optional<Measure> maxTimestampMeasure = measures.stream().max(Comparator.comparing(Measure::getTimestamp));
			if (maxTimestampMeasure.isPresent()) {
				minTimestamp = Math.min(minTimestamp, maxTimestampMeasure.get().getTimestamp());
			}
		}

//		for (int i = 1; i < connectedAnchors.size(); ++i) {
//			Integer anchorId = connectedAnchors.get(i);
//			float dist1 = measures[i].GetRange(timestamp);
//			Device_t anc1 = db.GetDevice(anc1_did);
//
//			for (int indo = 0; indo < i; ++indo) {
//				ushort anc2_did = connectedAnchorsDid[indo];
//				Device_t anc2 = db.GetDevice(anc2_did);
//				float dist2 = measures[indo].GetRange(timestamp);
//
//				if (full3D) {
//					for (int indu = 0; indu < indo; ++indu) {
//						ushort anc3_did = connectedAnchorsDid[indu];
//						Device_t anc3 = db.GetDevice(anc3_did);
//						float dist3 = measures[indu].GetRange(timestamp);
//						GetIntersections3d(anc1, dist1, anc2, dist2, anc3, dist3, ref ip);
//					}
//				} else {
//					GetIntersections(anc1, dist1, anc2, dist2, ref ip);
//				}
//			}
//		}
		// todo
		return Optional.empty();
	}
}
