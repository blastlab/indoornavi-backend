package co.blastlab.serviceblbnavi.socket.info.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Version implements CommandResponse {

	private String serial;
	private Integer shortId;

	@Override
	public void fromDescriptor(List<String> descriptor) {
		// version did:%X r:%s hV::%d.%d fV:%d.%d.%X serial:%s
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("serial")) {
				this.serial = value;
			}
			if (key.toLowerCase().equals("did")) {
				this.shortId = Integer.parseInt(value, 16);
			}
		});
	}
}
