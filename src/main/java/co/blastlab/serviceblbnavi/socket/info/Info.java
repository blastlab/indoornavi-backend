package co.blastlab.serviceblbnavi.socket.info;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Info {
	private Integer code;

	public enum InfoType {
		STATION_WAKE_UP(0),
		STATION_SLEEP(1),
		SINK_DID(2),
		VERSION(3),
		NEW_DEVICE(4),
		STATUS(5),
		FIRMWARE_UPGRADE(20),
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