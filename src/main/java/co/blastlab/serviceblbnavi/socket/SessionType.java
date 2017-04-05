package co.blastlab.serviceblbnavi.socket;

import lombok.Getter;

enum SessionType {
	ANCHOR("anchor"),
	TAG("tag"),
	SINK("sink");

	@Getter
	private final String name;

	SessionType(String name) {
		this.name = name;
	}
}
