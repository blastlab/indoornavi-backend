package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesWrapper extends MessageWrapper {

	private CoordinatesDto coordinates;
	private Point sinkPosition;

	public CoordinatesWrapper(CoordinatesDto coordinates, Sink sink) {
		super(MessageType.COORDINATES);
		this.coordinates = coordinates;
		this.sinkPosition = new Point(sink.getX(), sink.getY());
	}
}
