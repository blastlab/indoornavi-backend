package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AnchorDto extends DeviceDto {

	private Integer x;

	private Integer y;

	private Integer z;

	// these 2 fields are used in configuration
	@JsonProperty("xInPixels")
	private Integer xInPixels;

	@JsonProperty("yInPixels")
	private Integer yInPixels;

	public AnchorDto(Anchor anchor) {
		super(anchor);
		this.setX(anchor.getX());
		this.setY(anchor.getY());
		this.setZ(anchor.getZ());
	}
}