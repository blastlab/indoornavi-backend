package co.blastlab.serviceblbnavi.dto.uwb;

import co.blastlab.serviceblbnavi.domain.Uwb;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class UwbDto extends DeviceDto {

	@NotNull
	@ApiModelProperty(example = "14733")
	private Integer shortId;

	@ApiModelProperty(example = "12345adh5")
	private String firmwareVersion;

	public UwbDto(Uwb uwb) {
		super(uwb);
		this.setShortId(uwb.getShortId());
		this.setFirmwareVersion(uwb.getFirmwareVersion());
	}
}