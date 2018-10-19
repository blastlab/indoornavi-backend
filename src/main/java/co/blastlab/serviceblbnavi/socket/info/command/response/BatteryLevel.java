package co.blastlab.serviceblbnavi.socket.info.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatteryLevel implements CommandResponse {

	private Integer deviceShortId;
	private Uptime uptimeSeconds;

	@Override
	public void fromString(String descriptor) {
		// stat did:%x mV:%d Rx:%d Tx:%d Er:%d To:%d Uptime:%dd.%dh.%dm.%ds
		String[] parts = descriptor.split(" ");
		for (String part : parts) {
			String[] keyValue = part.split(":");
			String key = keyValue[0];
			String value = keyValue[1];
			if (key.equals("did")) {
				setDeviceShortId(Integer.valueOf(value));
			}
			if (key.equals("Uptime")) {
				String[] uptime = value.split("\\.");
				short days = Short.valueOf(uptime[0]);
				short hours = Short.valueOf(uptime[1]);
				short minutes = Short.valueOf(uptime[2]);
				short seconds = Short.valueOf(uptime[3]);
				setUptimeSeconds(new Uptime(days, hours, minutes, seconds));
			}
		}
	}

	@AllArgsConstructor
	@Getter
	static class Uptime {
		private short days;
		private short hours;
		private short minutes;
		private short seconds;
	}
}
