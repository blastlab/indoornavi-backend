package co.blastlab.serviceblbnavi.socket.measures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CoordinatesDto {
	private String device;
	private Double x;
	private Double y;
}
