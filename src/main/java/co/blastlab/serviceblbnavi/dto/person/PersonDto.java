package co.blastlab.serviceblbnavi.dto.person;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

@Getter
@Setter
abstract class PersonDto {

	@Email
	private String email;
}
