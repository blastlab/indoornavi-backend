package co.blastlab.serviceblbnavi.dto;

import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.socket.measures.Point3D;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoordinatesDto {
	private Long floorId;
	private Point3D point;
	private Date date;

	public CoordinatesDto(Coordinates coordinates) {
		this.floorId = coordinates.getFloor() != null ? coordinates.getFloor().getId() : null;
		this.point = new Point3D(coordinates.getX(), coordinates.getY(), coordinates.getZ());
		this.date = coordinates.getCreationDate();
	}
}
