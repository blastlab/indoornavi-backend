package co.blastlab.indoornavi.socket.info.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VersionCommand implements CommandResponse {

	private String serial;
	private Integer shortId;
	private Integer minor;
	private Integer major;
	private String commitHash;

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
			if (key.toLowerCase().equals("fv")) {
				String[] split = value.split("\\.");
				this.major = Integer.valueOf(split[0]);
				this.minor = Integer.valueOf(split[1]);
				this.commitHash = split[2];
			}
		});
	}
}
