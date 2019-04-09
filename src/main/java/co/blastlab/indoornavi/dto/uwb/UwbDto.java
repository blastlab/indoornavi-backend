package co.blastlab.indoornavi.dto.uwb;

import co.blastlab.indoornavi.domain.Uwb;
import co.blastlab.indoornavi.dto.device.DeviceDto;
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
