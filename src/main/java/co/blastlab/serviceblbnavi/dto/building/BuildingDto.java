package co.blastlab.serviceblbnavi.dto.building;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuildingDto {

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long complexId;

	public BuildingDto(Building building) {
		this.setName(building.getName());
		this.setComplexId(building.getComplex() != null ? building.getComplex().getId() : null);
	}


	@Getter
	@Setter
	@NoArgsConstructor
	@ApiModel(value = "BuildingId")
	public static class WithId extends BuildingDto {

		@ApiModelProperty(example = "1")
		private Long id;

		public WithId(Building building) {
			super(building);
			this.setId(building.getId());
		}


		@Getter
		@Setter
		@NoArgsConstructor
		public static class WithFloors extends WithId {
			private List<FloorDto> floors = new ArrayList<>();

			public WithFloors(Building building) {
				super(building);
				building.getFloors().forEach(floor -> this.getFloors().add(new FloorDto(floor)));
			}
		}
	}
}