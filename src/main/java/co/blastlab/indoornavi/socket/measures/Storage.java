package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.socket.measures.model.Measure;
import co.blastlab.indoornavi.socket.measures.model.PointAndTime;
import co.blastlab.indoornavi.socket.measures.model.PolyMeasure;
import lombok.Getter;

import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class Storage {
	// tag short id, anchor short id, measure list
	@Getter
	private Map<Integer, Map<Integer, PolyMeasure>> measures = new LinkedHashMap<>();
	@Getter
	private Map<Integer, PointAndTime> previousCoordinates = new HashMap<>();

	public double getDistance(Integer tagId, Integer anchorId) {
		double meanDistance = 0d;
		if (measures.containsKey(tagId)) {
			Map<Integer, PolyMeasure> anchorsMeasures = measures.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
				meanDistance = measures.stream().mapToDouble(Measure::getDistance).sum() / measures.size();
			}
		}
		return meanDistance;
	}
}
