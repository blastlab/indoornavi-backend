package co.blastlab.indoornavi.dto.complex;

import co.blastlab.indoornavi.domain.Building;
import co.blastlab.indoornavi.domain.Complex;
import co.blastlab.indoornavi.dto.building.BuildingDto;
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
			complex.getBuildings().forEach(building -> this.getBuildings().add(createBuilding(building)));
		}

		protected BuildingDto createBuilding(Building building) {
			return new BuildingDto(building);
		}

		@Getter
		@Setter
		@NoArgsConstructor
		public static class WithFloors extends WithBuildings {

			public WithFloors(Complex complex) {
				super(complex);
			}

			@Override
			protected BuildingDto createBuilding(Building building) {
				return new BuildingDto.WithFloors(building);
			}
		}
	}
}
