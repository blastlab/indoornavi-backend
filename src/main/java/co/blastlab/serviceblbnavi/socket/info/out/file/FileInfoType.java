package co.blastlab.serviceblbnavi.socket.info.out.file;

import java.util.HashMap;
import java.util.Map;

public enum FileInfoType {
	INFO(0),
	DOWNLOAD(1),
	ASK_LIST(2),
	LIST(3),
	DELETE(4);

	private final int value;
	FileInfoType(final int newValue) {
		this.value = newValue;
	}

	private static final Map<Integer, FileInfoType> map = new HashMap<>();
	static
	{
		for (FileInfoType infoType : FileInfoType.values())
			map.put(infoType.getValue(), infoType);
	}

	public int getValue() {
		return value;
	}

	public static FileInfoType from(int value) {
		return map.get(value);
	}
}
