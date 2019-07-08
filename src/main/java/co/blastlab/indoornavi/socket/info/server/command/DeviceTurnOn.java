package co.blastlab.indoornavi.socket.info.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTurnOn implements CommandResponse {

	private Integer deviceShortId;
	private Integer firmwareMinor;

	@Override
	public void fromDescriptor(List<String> descriptor) {
		// Device turn on did:4C fV:4
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("did")) {
				setDeviceShortId(Integer.valueOf(value, 16));
			}
			if (key.toLowerCase().equals("fv")) {
				setFirmwareMinor(Integer.parseInt(value));
			}
		});
	}

}
