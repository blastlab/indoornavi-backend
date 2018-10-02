package co.blastlab.serviceblbnavi.socket.device;

import lombok.Getter;

enum SessionType {
	ANCHOR("anchors"),
	TAG("tags"),
	SINK("sinks"),
	BLUETOOTH("bluetooths");

	@Getter
	private final String name;

	SessionType(String name) {
		this.name = name;
	}
}
