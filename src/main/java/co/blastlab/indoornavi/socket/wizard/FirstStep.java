package co.blastlab.indoornavi.socket.wizard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class FirstStep extends WizardStep {
	private Integer sinkShortId;
	private Long floorId;
}
