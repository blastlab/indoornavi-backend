package co.blastlab.serviceblbnavi.dto.floor;

import co.blastlab.serviceblbnavi.domain.Scale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ScaleDto {
	@NotNull
	private Point start;
	@NotNull
	private Point stop;

	/*
		How many pixels equals one measure unit
	 */
	@NotNull
	private Integer scale;

	@NotNull
	private Measure measure = Measure.MM;

	public ScaleDto(Scale scale) {
		this.setStart(new Point(scale.getStartX(), scale.getStartY()));
		this.setStop(new Point(scale.getStopX(), scale.getStopY()));
		this.setScale(scale.getScale());
		this.setMeasure(scale.getMeasure());
	}
}
