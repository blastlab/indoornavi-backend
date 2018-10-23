package co.blastlab.serviceblbnavi.socket.info.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatteryLevel implements CommandResponse {

	private Integer deviceShortId;
	private Uptime uptime;
	private Double percentage;

	private static int MAX_MV_WITHOUT_BATTERY = 5200;
	private static int MIN_MV_WITHOUT_BATTERY = 4500;
	private static int MAX_MV_WITH_BATTERY = 4300;
	private static int MIN_MV_WITH_BATTERY = 3100;

	@Override
	public void fromString(String descriptor) {
		// stat did:%x mV:%d Rx:%d Tx:%d Er:%d To:%d Uptime:%dd.%dh.%dm.%ds
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("did")) {
				setDeviceShortId(Integer.valueOf(value));
			}
			if (key.toLowerCase().equals("uptime")) {
				String[] uptime = value.split("\\.");
				short days = Short.valueOf(uptime[0]);
				short hours = Short.valueOf(uptime[1]);
				short minutes = Short.valueOf(uptime[2]);
				short seconds = Short.valueOf(uptime[3]);
				setUptime(new Uptime(days, hours, minutes, seconds));
			}
			if (key.toLowerCase().equals("mv")) {
				Integer mV = Integer.valueOf(value);
				if (isWithBattery(mV)) {
					percentage = (double) (((MAX_MV_WITH_BATTERY - MIN_MV_WITH_BATTERY) / 100) * mV);
				} else {
					percentage = (double) (((MAX_MV_WITHOUT_BATTERY - MIN_MV_WITHOUT_BATTERY) / 100) * mV);
				}
			}
		});
	}

	private boolean isWithBattery(Integer mV) {
		return Range.between(3100, 4300).contains(mV);
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
