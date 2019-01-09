package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.uwb.UwbDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AnchorDto extends UwbDto {

	private Integer x;

	private Integer y;

	private Integer z;

	// these 2 fields are used in configuration
	@JsonProperty("xInPixels")
	private Integer xInPixels;

	@JsonProperty("yInPixels")
	private Integer yInPixels;

	@ApiModelProperty(example = "1")
	private FloorDto floor;

	public AnchorDto(Anchor anchor) {
		super(anchor);
		this.setFloor(anchor.getFloor() != null ? new FloorDto(anchor.getFloor()) : null);
		this.setX(anchor.getX());
		this.setY(anchor.getY());
		this.setZ(anchor.getZ());
	}
}
