package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.socket.measures.model.Measure;
import co.blastlab.indoornavi.socket.measures.model.PointAndTime;
import co.blastlab.indoornavi.socket.measures.model.PolyMeasure;
import lombok.Getter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import javax.ejb.Singleton;
import java.util.*;

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

	public double getInterpolatedDistance(Integer tagId, Integer anchorId, long timestamp) {
		PolyMeasure polyMeasure = getMeasures().get(tagId).get(anchorId);
		List<Measure> measures = polyMeasure.getMeasures();
		if (measures.size() < 2 || measures.get(measures.size() - 1).getTimestamp() == timestamp) {
			return measures.get(measures.size() - 1).getDistance();
		}
		PolynomialFunction polyFn = new PolynomialFunction(polyMeasure.getPoly());
		return polyFn.value(timestamp - polyMeasure.getPolyCalculationTimestamp());
	}

	public long getTimeOfLastMeasure(Integer tagId) {
		long minTimestamp = new Date().getTime();
		for (Map.Entry<Integer, PolyMeasure> entry : getMeasures().get(tagId).entrySet()) {
			List<Measure> measures = entry.getValue().getMeasures();
			Optional<Measure> maxTimestampMeasure = measures.stream().max(Comparator.comparing(Measure::getTimestamp));
			if (maxTimestampMeasure.isPresent()) {
				minTimestamp = Math.min(minTimestamp, maxTimestampMeasure.get().getTimestamp());
			}
		}
		return minTimestamp;
	}

	public void setConnection(int tagId, int anchorId, double distance) {
		long now = new Date().getTime();

		if (measures.containsKey(tagId)) {
			Map<Integer, PolyMeasure> anchorsMeasures = measures.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
				anchorsMeasures.get(anchorId).setPoly(calculatePoly(measures, now));
				anchorsMeasures.get(anchorId).setPolyCalculationTimestamp(now);
				measures.add(new Measure(distance, now));
			} else {
				anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}, now));
			}
		} else {
			Map<Integer, PolyMeasure> anchorsMeasures = new LinkedHashMap<>();
			anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}, now));
			measures.put(tagId, anchorsMeasures);
		}
	}

	private double[] calculatePoly(List<Measure> measures, long now) {
		if (measures.size() == 1) {
			return new double[] {measures.get(0).getDistance()};
		}
		int polyOrder = 2;
		if (measures.size() < polyOrder) {
			polyOrder = measures.size();
		}
		double T = measures.get(measures.size() - 1).getTimestamp() - measures.get(0).getTimestamp();
		List<WeightedObservedPoint> points = new ArrayList<>();
		double weightMin = 0.3;
		int i = 0;
		for (Measure measure : measures) {
			double x = measure.getTimestamp() - now;
			double y = measure.getDistance();
			double weight = (1 - weightMin) * (measure.getTimestamp() - measures.get(0).getTimestamp()) / T + weightMin;
			points.add(new WeightedObservedPoint(weight, x, y));
		}

		PolynomialCurveFitter polyFitter = PolynomialCurveFitter.create(polyOrder - 1);
		return polyFitter.fit(points);
	}
}
