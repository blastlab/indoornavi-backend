package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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

	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithBuildings extends ComplexDto {
		private List<BuildingDto> buildings = new ArrayList<>();

		public WithBuildings(Complex complex, List<String> permissions) {
			super(complex, permissions);
			complex.getBuildings().forEach(building -> this.getBuildings().add(new BuildingDto(building)));
		}
	}
}
