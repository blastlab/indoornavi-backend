package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AnchorDto extends DeviceDto {

	private Integer x;

	private Integer y;

	public AnchorDto(Anchor anchor) {
		super(anchor);
		this.setX(anchor.getX());
		this.setY(anchor.getY());
	}
}