package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesWrapper extends MessageWrapper {

	private CoordinatesDto coordinates;

	public CoordinatesWrapper(CoordinatesDto coordinates) {
		super(MessageType.COORDINATES);
		this.coordinates = coordinates;
	}
}
