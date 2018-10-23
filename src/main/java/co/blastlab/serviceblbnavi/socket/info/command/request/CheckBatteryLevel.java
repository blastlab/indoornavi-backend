package co.blastlab.serviceblbnavi.socket.info.command.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckBatteryLevel extends CommandRequestBase implements CommandRequest {
	private String serial;
	private Integer shortId;

	@Override
	public String toStringCommand() {
		return String.format("stat did:%s", Integer.toHexString(this.getShortId()));
	}
}
