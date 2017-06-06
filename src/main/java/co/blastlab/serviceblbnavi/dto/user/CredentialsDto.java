package co.blastlab.serviceblbnavi.dto.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CredentialsDto {
	@Size(min = 1)
	@NotNull
	private String username;
	@Size(min = 1)
	@NotNull
	private String plainPassword;
}
