package co.blastlab.indoornavi.dto.floor;

import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.building.BuildingDto;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FloorDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@ApiModelProperty(example = "0")
	private Integer level;

	private String name;

	@NotNull
	private BuildingDto building;

	@ApiModelProperty(example = "1")
	private Long imageId;

	private ScaleDto scale;

	private String displayName;

	public FloorDto(Long id, Integer level, String name, BuildingDto building, Long imageId, ScaleDto scale) {
		this.setId(id);
		this.setLevel(level);
		this.setName(name);
		this.setBuilding(building);
		this.setImageId(imageId);
		this.setScale(scale);
	}

	public FloorDto(Floor floor) {
		this.setId(floor.getId());
		this.setLevel(floor.getLevel());
		this.setName(floor.getName());
		this.setBuilding(floor.getBuilding() != null ? new BuildingDto(floor.getBuilding()) : null);
		this.setImageId(floor.getImage() != null ? floor.getImage().getId() : null);
		this.setScale(floor.getScale() != null ? new ScaleDto(floor.getScale()) : null);
		this.setDisplayName(this.buildDisplayName(floor));
	}

	private String buildDisplayName(Floor floor) {
		StringBuilder sb = new StringBuilder();
		if (floor.getBuilding() != null) {
			if (floor.getBuilding().getComplex() != null) {
				sb.append(floor.getBuilding().getComplex().getName());
				sb.append("/");
			}
			sb.append(floor.getBuilding().getName());
			sb.append("/");
			sb.append(floor.getLevel());
		}
		if (!Strings.isNullOrEmpty(floor.getName())) {
			sb.append("/");
			sb.append(floor.getName());
		}
		return sb.toString();
	}
}
