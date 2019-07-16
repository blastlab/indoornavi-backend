package pl.indoornavi.coordinatescalculator.shared;

import lombok.Getter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.indoornavi.coordinatescalculator.models.Measure;
import pl.indoornavi.coordinatescalculator.models.PointAndTime;
import pl.indoornavi.coordinatescalculator.models.PolyMeasure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Storage {

	// 10 seconds
	private final static long OLD_DATA_IN_MILLISECONDS = 10_000;

	// tag short id, anchor short id, measure list
	@Getter
	private Map<Integer, Map<Integer, PolyMeasure>> measures = new ConcurrentHashMap<>();
	@Getter
	private Map<Integer, PointAndTime> previousCoordinates = new ConcurrentHashMap<>();

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

	public void setConnection(int tagId, int anchorId, double distance, long measurementTime) {
		if (measures.containsKey(tagId)) {
			Map<Integer, PolyMeasure> anchorsMeasures = measures.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
				calculatePoly(measures, measurementTime).ifPresent(poly -> {
					anchorsMeasures.get(anchorId).setPoly(poly);
					anchorsMeasures.get(anchorId).setPolyCalculationTimestamp(measurementTime);
					measures.add(0, new Measure(distance, measurementTime));
				});
			} else {
				anchorsMeasures.put(anchorId, new PolyMeasure(
					new LinkedList<>(Collections.singletonList(new Measure(distance, measurementTime))),
					new double[]{distance}, measurementTime));
			}
		} else {
			Map<Integer, PolyMeasure> anchorsMeasures = new HashMap<>();
			anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, measurementTime))), new double[]{distance}, measurementTime));
			this.measures.put(tagId, anchorsMeasures);
		}
	}

	public Set<Integer> getConnectedAnchors(int tagId, Long measurementTime) {
		cleanOldData(tagId, measurementTime);
		Set<Integer> connectedAnchors = new HashSet<>();
		if (getMeasures().containsKey(tagId)) {
			connectedAnchors.addAll(getMeasures().get(tagId).keySet());
		}
		return connectedAnchors;
	}

	private Optional<double[]> calculatePoly(List<Measure> measures, long measurementTime) {
		if (measures.size() == 1) {
			return Optional.of(new double[] {measures.get(0).getDistance()});
		}
		int polyOrder = 2;
		if (measures.size() < polyOrder) {
			polyOrder = measures.size();
		}
		Optional<Measure> newestMeasure = measures.stream().max(Comparator.comparing(Measure::getTimestamp));
		Optional<Measure> oldestMeasure = measures.stream().min(Comparator.comparing(Measure::getTimestamp));
		if (newestMeasure.isPresent() && oldestMeasure.isPresent()) {
			double newestTimestamp = newestMeasure.get().getTimestamp();
			double oldestTimestamp = oldestMeasure.get().getTimestamp();
			double T = newestTimestamp - oldestTimestamp;
			List<WeightedObservedPoint> points = new ArrayList<>();
			double weightMin = 0.3;
			for (Measure measure : measures) {
				double x = measure.getTimestamp() - measurementTime;
				double y = measure.getDistance();
				double weight = (1 - weightMin) * (measure.getTimestamp() - oldestTimestamp) / T + weightMin;
				points.add(new WeightedObservedPoint(weight, x, y));
			}

			PolynomialCurveFitter polyFitter = PolynomialCurveFitter.create(polyOrder - 1);
			return Optional.of(polyFitter.fit(points));
		}

		return Optional.empty();
	}

	private void cleanOldData(Integer tagId, Long measurementTime) {
		Map<Integer, PolyMeasure> tagMeasures = getMeasures().get(tagId);
		tagMeasures.forEach((anchor, polyMeasure) -> {
			polyMeasure.getMeasures().removeIf((measure) -> measurementTime - OLD_DATA_IN_MILLISECONDS > measure.getTimestamp());
			while (polyMeasure.getMeasures().size() > 4) {
				polyMeasure.getMeasures().remove(polyMeasure.getMeasures().size() - 1);
			}
		});

		tagMeasures.values().removeIf((polyMeasure ->
			polyMeasure.getMeasures().isEmpty()
		));
	}

}
