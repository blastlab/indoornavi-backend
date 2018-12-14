package co.blastlab.serviceblbnavi.socket.info.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "shortId")
public class CheckBatteryLevel implements CommandRequest {
	private Integer shortId;

	@Override
	public String toStringCommand() {
		return String.format("stat did:%s", Integer.toHexString(this.getShortId()));
	}
}
