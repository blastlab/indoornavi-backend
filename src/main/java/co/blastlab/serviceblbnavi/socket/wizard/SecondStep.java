package co.blastlab.serviceblbnavi.socket.wizard;

import co.blastlab.serviceblbnavi.dto.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class SecondStep extends WizardStep {
	private Point sinkPosition;
	private Integer anchorShortId;
	private Double degree;
}
