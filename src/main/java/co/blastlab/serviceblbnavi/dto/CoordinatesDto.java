package co.blastlab.serviceblbnavi.dto;

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
