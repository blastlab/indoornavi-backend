package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComplexDto {

	public ComplexDto(Complex complex, List<String> permissions) {
		this.setId(complex.getId());
		this.setName(complex.getName());
		this.getPermissions().addAll(permissions);
	}

	private Long id;

	@NotNull
	@NotEmpty
	private String name;

	@ApiModelProperty(readOnly = true)
	private List<String> permissions = new ArrayList<>();
}
