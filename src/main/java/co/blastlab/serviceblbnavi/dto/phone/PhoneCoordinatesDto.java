package co.blastlab.serviceblbnavi.dto.phone;

import co.blastlab.serviceblbnavi.domain.PhoneCoordinates;
import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.utils.DateConverter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PhoneCoordinatesDto extends CoordinatesDto {
	private Long phoneId;

	public PhoneCoordinatesDto(PhoneCoordinates phoneCoordinates) {
		super(phoneCoordinates);
		this.phoneId = phoneCoordinates.getPhone() != null ? phoneCoordinates.getPhone().getId() : null;
	}
}
