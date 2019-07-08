package co.blastlab.indoornavi.socket.info.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Beacon implements CommandResponse {
	private Integer deviceShortId;

	@Override
	public void fromDescriptor(List<String> descriptor) {
		// Beacon from did:8022 mV:4376 route:[8022] serial:3841501300620064
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("did")) {
				setDeviceShortId(Integer.valueOf(value, 16));
			}
		});
	}
}
