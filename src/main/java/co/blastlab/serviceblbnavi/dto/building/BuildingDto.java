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
		this.setId(building.getId());
		this.setName(building.getName());
		building.getFloors().forEach((floor -> this.getFloors().add(new FloorDto(floor))));
	}

	private Long id;

	@NotNull
	@NotEmpty
	private String name;

	private List<FloorDto> floors = new ArrayList<>();

	@Getter
	@Setter
	@NoArgsConstructor
	public static class New extends BuildingDto {

		@NotNull
		@ApiModelProperty(example = "1")
		private Long complexId;

		public New(Building building) {
			super(building);
			this.setComplexId(building.getComplex() != null ? building.getComplex().getId() : null);
		}
	}
}
