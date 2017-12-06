package co.blastlab.serviceblbnavi.dto.report;

import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CoordinatesDto {
	private Integer tagShortId;
	private Long floorId;
	private Point point;
	private Date date;

	public CoordinatesDto(Coordinates coordinates) {
		this.tagShortId = coordinates.getTag().getShortId();
		this.floorId = coordinates.getFloor().getId();
		this.point = new Point(coordinates.getX(), coordinates.getY());
		this.date = coordinates.getCreationDate();
	}
}
