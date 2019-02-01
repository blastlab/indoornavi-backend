package co.blastlab.indoornavi.socket.info.server.update;

import co.blastlab.indoornavi.socket.info.server.Info;

import java.util.HashMap;
import java.util.Map;

public class UpdateInfo extends Info {

	public UpdateInfo() { super(InfoType.FIRMWARE_UPDATE.getValue()); }

	public enum UpdateInfoType {
		INFO(0),
		START(1),
		ACK(2);

		private final int value;
		UpdateInfoType(final int newValue) {
			this.value = newValue;
		}

		private static final Map<Integer, UpdateInfoType> map = new HashMap<>();
		static
		{
			for (UpdateInfoType updateInfoType : UpdateInfoType.values())
				map.put(updateInfoType.getValue(), updateInfoType);
		}

		public int getValue() {
			return value;
		}

		public static UpdateInfoType from(int value) {
			return map.get(value);
		}
	}
}


