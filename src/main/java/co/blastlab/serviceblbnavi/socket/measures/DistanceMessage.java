package co.blastlab.serviceblbnavi.socket.measures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistanceMessage {
	private Integer did1;
	private Integer did2;
	private Integer dist;
	private Double signal;
}
