package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
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

		@Getter
		@Setter
		@NoArgsConstructor
		public static class WithFloors extends WithBuildings {

			private List<FloorDto> floors = new ArrayList<>();

			public WithFloors(Complex complex) {
				super(complex);
				complex.getBuildings().forEach(building -> building.getFloors().forEach(floor -> {
						this.getFloors().add(new FloorDto(floor));
					})
				);
			}
		}
	}
}