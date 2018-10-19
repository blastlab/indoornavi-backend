package co.blastlab.serviceblbnavi.socket.info.command.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckBatteryLevel extends CommandRequestBase implements CommandRequest {
	private String sinkName;
	private Integer sinkShortId;

	@Override
	public String toString() {
		return String.format("stat did:%s", Integer.toHexString(this.getSinkShortId()));
	}
}
