package pl.indoornavi.coordinatescalculator.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UwbCoordinatesDto {
	private Integer tagShortId;
	private Integer anchorShortId;
	private Long floorId;
	private Point3D point;
	private Date measurementTime;
}
