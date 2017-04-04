package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnchorDto extends DeviceDto {

	private Double x;

	private Double y;

	@NotNull
	@ApiModelProperty(example = "false")
	private Boolean verified;

	public AnchorDto(Anchor anchor) {
		this.setId(anchor.getId());
		this.setShortId(anchor.getShortId());
		this.setLongId(anchor.getLongId());
		this.setName(anchor.getName());
		this.setFloorId(anchor.getFloor() != null ? anchor.getFloor().getId() : null);
		this.setX(anchor.getX());
		this.setY(anchor.getY());
		this.verified = anchor.getVerified();
	}
}