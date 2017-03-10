package co.blastlab.serviceblbnavi.dto.floor;

import co.blastlab.serviceblbnavi.domain.Floor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class FloorDto {

	public FloorDto(Floor floor) {
		this.setId(floor.getId());
		this.setBuildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null);
	}

	// TODO: on every field that is in some metric, should be description metric it uses i.e. bitmapWidth should be described: px

	@ApiModelProperty(example = "1")
	private Long id;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long buildingId;
}
