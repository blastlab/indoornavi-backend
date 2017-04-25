package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CoordinatesWrapper extends MessageDto {

	private CoordinatesDto coordinatesDto;

	public CoordinatesWrapper(TypeMessage type, CoordinatesDto coordinatesDto) {
		super(type);
		this.coordinatesDto = coordinatesDto;
	}
}
