package co.blastlab.serviceblbnavi.dto.bluetooth;

import co.blastlab.serviceblbnavi.domain.Bluetooth;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BluetoothDto extends DeviceDto {

	@ApiModelProperty(example = "14733")
	private Integer minor;
	@ApiModelProperty(example = "9982")
	private Integer major;
	@ApiModelProperty(example = "40")
	private Short powerTransmission;

	public BluetoothDto(Bluetooth bluetooth) {
		super(bluetooth);
		this.major = bluetooth.getMajor();
		this.minor = bluetooth.getMinor();
		this.powerTransmission = bluetooth.getPower();
	}
}
