package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CoordinatesWrapper extends MessageWrapper {

	private UwbCoordinatesDto coordinates;

	public CoordinatesWrapper(UwbCoordinatesDto coordinates) {
		super(MessageType.COORDINATES);
		this.coordinates = coordinates;
	}
}
