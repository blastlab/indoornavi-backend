package co.blastlab.serviceblbnavi.socket.info.server.version;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Version {

	private Integer shortId;
	private Role role;
	private String hardwareVersion;
	private String firmwareVersion;

	public enum Role {
		SINK(0),
		ANCHOR(1),
		TAG(2);

		private final int value;

		Role(final int newValue) {
			this.value = newValue;
		}

		private static final Map<Integer, Role> map = new HashMap<>();

		static {
			for (Role role : Role.values()) {
				map.put(role.getValue(), role);
			}
		}

		public int getValue() {
			return value;
		}

		public static Role from(int value) {
			return map.get(value);
		}
	}
}
