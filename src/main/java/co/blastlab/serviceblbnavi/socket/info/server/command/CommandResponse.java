package co.blastlab.serviceblbnavi.socket.info.server.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommandResponse {
	void fromDescriptor(List<String> descriptor);
	default Map<String, String> getParameters(List<String> descriptor) {
		Map<String, String> parameters = new HashMap<>();
		for (String part : descriptor) {
			String[] keyValue = part.split(":");
			String key = keyValue[0];
			String value = keyValue[1];
			parameters.put(key, value);
		}
		return parameters;
	}
}
