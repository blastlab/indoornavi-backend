package co.blastlab.serviceblbnavi.dto.person;

import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

@Getter
@Setter
abstract class PersonDto {

	@Email
	@ApiModelProperty(example = "user@email.com")
	private String email;
}
