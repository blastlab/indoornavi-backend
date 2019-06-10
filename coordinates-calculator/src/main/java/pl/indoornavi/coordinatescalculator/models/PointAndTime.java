package pl.indoornavi.coordinatescalculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PointAndTime {
	private Point3D point;
	private long timestamp;
}
