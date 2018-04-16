package co.blastlab.serviceblbnavi.dto.device;

import co.blastlab.serviceblbnavi.domain.Device;
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
public class DeviceDto {

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@ApiModelProperty(example = "14733")
	private Integer shortId;

	@NotNull
	@ApiModelProperty(example = "87367834748864600")
	private Long longId;

	@ApiModelProperty(example = "Name")
	private String name;

	@ApiModelProperty(example = "1")
	private Long floorId;

	@NotNull
	@ApiModelProperty(example = "false")
	private Boolean verified;

	@ApiModelProperty(example = "")
	private String firmwareVersion;

	public DeviceDto(Device device) {
		this.setId(device.getId());
		this.setShortId(device.getShortId());
		this.setLongId(device.getLongId());
		this.setName(device.getName());
		this.setFloorId(device.getFloor() != null ? device.getFloor().getId() : null);
		this.setVerified(device.getVerified());
		this.setFirmwareVersion(device.getFirmwareVersion());
	}
}