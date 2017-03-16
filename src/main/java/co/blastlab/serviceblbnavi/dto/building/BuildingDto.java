package co.blastlab.serviceblbnavi.dto.building;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import io.swagger.annotations.ApiModelProperty;
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
public class BuildingDto {

	public BuildingDto(Building building) {
		this.setName(building.getName());
		this.setComplexId(building.getComplex() != null ? building.getComplex().getId() : null);
		building.getFloors().forEach((floor -> this.getFloors().add(new FloorDto(floor))));
	}

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long complexId;

	private List<FloorDto> floors = new ArrayList<>();

	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithId extends BuildingDto {

		private Long id;

		public WithId(Building building) {
			super(building);
			this.setId(building.getId());
		}
	}
}
