package pl.indoornavi.coordinatescalculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PolyMeasure {
	private List<Measure> measures;
	private double[] poly;
	private long polyCalculationTimestamp;
}
