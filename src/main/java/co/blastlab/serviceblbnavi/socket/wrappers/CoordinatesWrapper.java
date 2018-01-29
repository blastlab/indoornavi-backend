package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesWrapper extends MessageWrapper {

	private CoordinatesDto coordinates;
	private Integer sinkShortId;

	public CoordinatesWrapper(CoordinatesDto coordinates, Integer sinkShortId) {
		super(MessageType.COORDINATES);
		this.coordinates = coordinates;
		this.sinkShortId = sinkShortId;
	}
}
