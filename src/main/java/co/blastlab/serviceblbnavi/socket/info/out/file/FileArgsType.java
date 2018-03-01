package co.blastlab.serviceblbnavi.socket.info.out.file;

import java.util.HashMap;
import java.util.Map;

public enum FileArgsType {
	INFO(0),
	DOWNLOAD(1),
	ASK_LIST(2),
	LIST(3),
	DELETE(4);

	private final int value;
	FileArgsType(final int newValue) {
		this.value = newValue;
	}

	private static final Map<Integer, FileArgsType> map = new HashMap<>();
	static
	{
		for (FileArgsType infoType : FileArgsType.values())
			map.put(infoType.getValue(), infoType);
	}

	public int getValue() {
		return value;
	}

	public static FileArgsType from(int value) {
		return map.get(value);
	}
}
