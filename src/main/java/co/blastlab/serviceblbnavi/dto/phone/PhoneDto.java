package co.blastlab.serviceblbnavi.dto.phone;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhoneDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;
	@ApiModelProperty(example = "User defined data")
	@Size(max = 255)
	private String userData;
}
