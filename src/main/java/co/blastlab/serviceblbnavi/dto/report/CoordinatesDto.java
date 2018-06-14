package co.blastlab.serviceblbnavi.dto.report;

import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.Point;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoordinatesDto {
	private Integer tagShortId;
	private Integer anchorShortId;
	private Long floorId;
	private Point point;
	private Date date;

	public CoordinatesDto(Coordinates coordinates) {
		this.tagShortId = coordinates.getTag() != null ? coordinates.getTag().getShortId() : null;
		this.floorId = coordinates.getFloor() != null ? coordinates.getFloor().getId() : null;
		this.point = new Point(coordinates.getX(), coordinates.getY());
		this.date = coordinates.getCreationDate();
	}
}
