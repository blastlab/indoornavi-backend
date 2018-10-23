package co.blastlab.serviceblbnavi.socket.info.command.response;

import java.util.HashMap;
import java.util.Map;

public interface CommandResponse {
	void fromString(String descriptor);
	default Map<String, String> getParameters(String descriptor) {
		Map<String, String> parameters = new HashMap<>();
		String[] parts = descriptor.split(" ");
		for (String part : parts) {
			String[] keyValue = part.split(":");
			String key = keyValue[0];
			String value = keyValue[1];
			parameters.put(key, value);
		}
		return parameters;
	}
}
