package pl.indoornavi.coordinatescalculator.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UwbCoordinates {
	private Integer tagId;
	private Integer anchorId;
	private Long floorId;
	private int x;
	private int y;
	private int z;
	private Date time;
}
