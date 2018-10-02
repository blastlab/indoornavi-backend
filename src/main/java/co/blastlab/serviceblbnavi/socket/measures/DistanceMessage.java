package co.blastlab.serviceblbnavi.socket.measures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DistanceMessage {
	private Integer did1;
	private Integer did2;
	private Integer dist;
	private Double signal;
}
