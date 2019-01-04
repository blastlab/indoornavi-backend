package co.blastlab.serviceblbnavi.socket.info.server.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Range;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatteryLevel implements CommandResponse {

	private Integer deviceShortId;
	private Uptime uptime;
	private Double percentage;

	private static int MAX_MV_WITH_BATTERY = 4300;
	private static int MIN_MV_WITH_BATTERY = 3100;

	@Override
	public void fromDescriptor(List<String> descriptor) {
		// stat did:%x mV:%d Rx:%d Tx:%d Er:%d To:%d Uptime:%dd.%dh.%dm.%ds
		getParameters(descriptor).forEach((key, value) -> {
			if (key.toLowerCase().equals("did")) {
				setDeviceShortId(Integer.valueOf(value, 16));
			}
			if (key.toLowerCase().equals("uptime")) {
				String[] uptime = value.replaceAll("[^\\d.]", "").split("\\.");
				short days = Short.valueOf(uptime[0]);
				short hours = Short.valueOf(uptime[1]);
				short minutes = Short.valueOf(uptime[2]);
				short seconds = Short.valueOf(uptime[3]);
				setUptime(new Uptime(days, hours, minutes, seconds));
			}
			if (key.toLowerCase().equals("mv")) {
				Integer mV = Integer.valueOf(value);
				if (isWithBattery(mV)) {
					percentage = (double) ((mV - MIN_MV_WITH_BATTERY) * 100) / (MAX_MV_WITH_BATTERY - MIN_MV_WITH_BATTERY);
				} else {
					percentage = (double) (mV > MAX_MV_WITH_BATTERY ? 100 : 0);
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
