package co.blastlab.indoornavi.dto.phone;

import co.blastlab.indoornavi.domain.PhoneCoordinates;
import co.blastlab.indoornavi.dto.CoordinatesDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PhoneCoordinatesDto extends CoordinatesDto {
	private Long phoneId;
	private LocalDateTime measurementTime;

	public PhoneCoordinatesDto(PhoneCoordinates phoneCoordinates) {
		super(phoneCoordinates);
		this.phoneId = phoneCoordinates.getPhone() != null ? phoneCoordinates.getPhone().getId() : null;
		this.measurementTime = phoneCoordinates.getMeasurementTime();
	}
}
