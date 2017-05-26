package co.blastlab.serviceblbnavi.socket.wizard;

import co.blastlab.serviceblbnavi.dto.floor.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class WizardStep {
	private Integer sinkShortId;
	private Point sinkPosition;
	private Integer anchorShortId;
	private Double degree;

	boolean isFirstStep() {
		return sinkShortId != null && sinkPosition == null && anchorShortId == null && degree == null;
	}

	boolean isSecondStep() {
		return sinkShortId != null && sinkPosition != null && anchorShortId != null && degree != null;
	}
}
