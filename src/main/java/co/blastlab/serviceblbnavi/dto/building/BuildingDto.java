package co.blastlab.serviceblbnavi.dto.building;

import co.blastlab.serviceblbnavi.domain.Building;
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
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long complexId;

	public BuildingDto(Building building) {
		this.setId(building.getId());
		this.setName(building.getName());
		this.setComplexId(building.getComplex() != null ? building.getComplex().getId() : null);
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithFloors extends BuildingDto {
		private List<FloorDto> floors = new ArrayList<>();

		public WithFloors(Building building) {
			super(building);
			building.getFloors().forEach(floor -> this.getFloors().add(new FloorDto(floor)));
		}
	}
}