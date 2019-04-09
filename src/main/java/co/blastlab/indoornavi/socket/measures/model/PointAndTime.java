package co.blastlab.indoornavi.socket.measures.model;

import co.blastlab.indoornavi.socket.measures.Point3D;
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
