package co.blastlab.serviceblbnavi.dto;

import co.blastlab.serviceblbnavi.domain.Coordinates;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoordinatesDto {
	private Long floorId;
	private Point point;
	private Date date;

	public CoordinatesDto(Coordinates coordinates) {
		this.floorId = coordinates.getFloor() != null ? coordinates.getFloor().getId() : null;
		this.point = new Point(coordinates.getX(), coordinates.getY());
		this.date = coordinates.getCreationDate();
	}
}
