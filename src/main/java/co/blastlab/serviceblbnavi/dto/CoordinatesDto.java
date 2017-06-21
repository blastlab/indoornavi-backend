package co.blastlab.serviceblbnavi.dto;

import co.blastlab.serviceblbnavi.dto.floor.Point;
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
