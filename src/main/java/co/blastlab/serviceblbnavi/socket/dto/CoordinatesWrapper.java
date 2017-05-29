package co.blastlab.serviceblbnavi.socket.dto;

import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesWrapper extends MessageWrapper {

	private CoordinatesDto coordinatesDto;

	public CoordinatesWrapper(CoordinatesDto coordinatesDto) {
		super(MessageType.COORDINATES);
		this.coordinatesDto = coordinatesDto;
	}
}
