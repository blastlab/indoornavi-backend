package co.blastlab.indoornavi.socket.measures.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PolyMeasure {
	private List<Measure> measures = new ArrayList<>();
	private double[] poly;
}
