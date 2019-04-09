package co.blastlab.indoornavi.dto.report;

import co.blastlab.indoornavi.domain.UwbCoordinates;
import co.blastlab.indoornavi.dto.CoordinatesDto;
import co.blastlab.indoornavi.socket.measures.Point3D;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UwbCoordinatesDto extends CoordinatesDto {
	private Integer tagShortId;
	private Integer anchorShortId;

	public UwbCoordinatesDto(UwbCoordinates uwbCoordinates) {
		super(uwbCoordinates);
		this.tagShortId = uwbCoordinates.getTag() != null ? uwbCoordinates.getTag().getShortId() : null;
	}

	public UwbCoordinatesDto(Integer tagId, Integer anchorId, Long floorId, Point3D newPoint, Date currentDate) {
		this.tagShortId = tagId;
		this.anchorShortId = anchorId;
		this.setFloorId(floorId);
		this.setPoint(newPoint);
		this.setDate(currentDate);
	}
}
