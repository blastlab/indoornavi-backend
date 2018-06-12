package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ComplexDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@NotEmpty
	private String name;

	public ComplexDto(Complex complex) {
		this.setId(complex.getId());
		this.setName(complex.getName());
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithBuildings extends ComplexDto {
		private List<BuildingDto> buildings = new ArrayList<>();

		public WithBuildings(Complex complex) {
			super(complex);
			complex.getBuildings().forEach(building -> this.getBuildings().add(new BuildingDto(building)));
		}
	}
}