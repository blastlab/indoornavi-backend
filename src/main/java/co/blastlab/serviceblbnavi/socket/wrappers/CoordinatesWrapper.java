package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CoordinatesWrapper extends MessageWrapper {

	private CoordinatesDto coordinates;

	public CoordinatesWrapper(CoordinatesDto coordinates) {
		super(MessageType.COORDINATES);
		this.coordinates = coordinates;
	}
}
