package co.blastlab.indoornavi.dto.floor;

import co.blastlab.indoornavi.domain.Scale;
import co.blastlab.indoornavi.dto.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScaleDto {
	@NotNull
	private Point start;
	@NotNull
	private Point stop;

	/*
		Distance provided by user
	 */
	@NotNull
	private Integer realDistance;

	@NotNull
	private Measure measure = Measure.METERS;

	public ScaleDto(Scale scale) {
		this.setStart(new Point(scale.getStartX(), scale.getStartY()));
		this.setStop(new Point(scale.getStopX(), scale.getStopY()));
		this.setRealDistance(scale.getMeasure().equals(Measure.METERS) ? scale.getRealDistanceInCentimeters() / 100 : scale.getRealDistanceInCentimeters());
		this.setMeasure(scale.getMeasure());
	}
}
