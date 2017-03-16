package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
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

	public ComplexDto(Complex complex) {
		this.setName(complex.getName());
	}

	@NotNull
	@NotEmpty
	private String name;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithId extends ComplexDto {
		private Long id;

		public WithId(Complex complex) {
			super(complex);
			this.setId(complex.getId());
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

}
