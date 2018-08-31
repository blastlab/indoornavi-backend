package co.blastlab.serviceblbnavi.dto.phone;

import co.blastlab.serviceblbnavi.domain.Phone;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhoneDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;
	@ApiModelProperty(example = "User defined data")
	private String userData;

	public PhoneDto(Phone phone) {
		this.id = phone.getId();
		this.userData = phone.getUserData();
	}
}
