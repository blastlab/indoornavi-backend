package co.blastlab.indoornavi.socket.info.controller;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum CommandType {
	DEVICE_TURN_ON("I1101"),
	BATTERY_LEVEL("I1111"),
	VERSION("I1112"),
	BEACON("I1103");

	private final String code;

	CommandType(final String code) {
		this.code = code;
	}

	private static final Map<String, CommandType> map = new HashMap<>();
	static
	{
		for (CommandType commandType : CommandType.values())
			map.put(commandType.getCode(), commandType);
	}

	public static CommandType byCode(String code) {
		return map.get(code);
	}
}
