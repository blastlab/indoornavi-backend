package co.blastlab.serviceblbnavi.socket.info.server.update;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdateInfoCode {
	private Short iCode;

	private Integer shortId;

	public enum UpdateInfoCodeType {
		SUCCESS(18),
		ABORTED(19),
		RETRANSMISSION(20);

		private final int value;
		UpdateInfoCodeType(final int newValue) {
			this.value = newValue;
		}

		private static final Map<Integer, UpdateInfoCodeType> map = new HashMap<>();
		static
		{
			for (UpdateInfoCodeType updateInfoCode : UpdateInfoCodeType.values())
				map.put(updateInfoCode.getValue(), updateInfoCode);
		}

		public int getValue() {
			return value;
		}

		public static UpdateInfoCodeType from(int value) {
			return map.get(value);
		}
	}
}


