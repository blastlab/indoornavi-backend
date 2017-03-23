package co.blastlab.serviceblbnavi.dto.floor;

import co.blastlab.serviceblbnavi.domain.Floor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@ApiModelProperty(example = "0")
	private Integer level;

	private String name;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long buildingId;

	public FloorDto(Floor floor) {
		this.setId(floor.getId());
		this.setLevel(floor.getLevel());
		this.setName(floor.getName());
		this.setBuildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null);
	}
}
