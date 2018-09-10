package co.blastlab.serviceblbnavi.dto.device;

import co.blastlab.serviceblbnavi.domain.Device;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeviceDto {
	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;
	@ApiModelProperty(example = "Name")
	private String name;
	@ApiModelProperty(example = "false")
	@NotNull
	private Boolean verified;
	@ApiModelProperty(example = "0a:14:22:0d:23:45")
	private String macAddress;

	public DeviceDto(Device device) {
		this.setId(device.getId());
		this.setMacAddress(device.getMac());
		this.setName(device.getName());
		this.setVerified(device.getVerified());
	}
}
