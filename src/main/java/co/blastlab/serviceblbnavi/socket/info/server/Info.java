package co.blastlab.serviceblbnavi.socket.info.server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Info extends InfoCode {
	private long time;
	private Object args;

	public Info(Integer code) {
		super(code);
		this.time = new Date().getTime();
	}

	public enum InfoType {
		INFO(0),
		BROADCAST(1),
		VERSION(2),
		STATUS(3),
		STATION_WAKE_UP(4),
		STATION_SLEEP(5),
		FIRMWARE_UPDATE(20),
		FILE(21);

		private final int value;
		InfoType(final int newValue) {
			this.value = newValue;
		}

		private static final Map<Integer, InfoType> map = new HashMap<>();
		static
		{
			for (InfoType infoType : InfoType.values())
				map.put(infoType.getValue(), infoType);
		}

		public int getValue() {
			return value;
		}

		public static InfoType from(int value) {
			return map.get(value);
		}
	}
}