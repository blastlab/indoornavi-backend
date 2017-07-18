package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dto.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CoordinatesDto {
	private Integer deviceId;
	private Point point;
}
