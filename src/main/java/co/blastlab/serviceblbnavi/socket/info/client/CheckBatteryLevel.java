package co.blastlab.serviceblbnavi.socket.info.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckBatteryLevel implements CommandRequest {
	private Integer shortId;

	@Override
	public String toStringCommand() {
		return String.format("stat did:%s", Integer.toHexString(this.getShortId()));
	}
}
