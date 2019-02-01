package co.blastlab.indoornavi.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CredentialsDto {
	@Size(min = 1)
	@NotNull
	@ApiModelProperty(example = "admin")
	private String username;
	@Size(min = 1)
	@NotNull
	@ApiModelProperty(example = "admin")
	private String plainPassword;
}
