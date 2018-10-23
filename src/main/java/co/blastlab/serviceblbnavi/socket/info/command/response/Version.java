package co.blastlab.serviceblbnavi.socket.info.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Version implements CommandResponse {

	private String serial;

	@Override
	public void fromString(String descriptor) {
		// version did:%X r:%s hV::%d.%d fV:%d.%d.%X serial:%s
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("serial")) {
				this.serial = value;
			}
		});
	}
}
