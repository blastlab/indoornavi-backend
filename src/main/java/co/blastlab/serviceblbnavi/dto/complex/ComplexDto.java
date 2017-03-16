package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import com.wordnik.swagger.annotations.ApiModel;
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

	@NotNull
	@NotEmpty
	private String name;

	public ComplexDto(Complex complex) {
		this.setName(complex.getName());
	}


	@Getter
	@Setter
	@NoArgsConstructor
	@ApiModel(value = "ComplexId") /* Konieczne ze względu na działanie Swaggera. Bez tej adnotacji model klasy ComplexDto.WithId.class był nadpisywany
	    przez model klasy BuildingDto.WithId.class, co było widoczne w pliku swagger.json */
	public static class WithId extends ComplexDto {

		@ApiModelProperty(example = "1")
		private Long id;

		public WithId(Complex complex) {
			super(complex);
			this.setId(complex.getId());
		}


		@Getter
		@Setter
		@NoArgsConstructor
		public static class WithBuildings extends WithId {
			private List<BuildingDto> buildings = new ArrayList<>();

			public WithBuildings(Complex complex) {
				super(complex);
				complex.getBuildings().forEach(building -> this.getBuildings().add(new BuildingDto(building)));
			}
		}
	}
}