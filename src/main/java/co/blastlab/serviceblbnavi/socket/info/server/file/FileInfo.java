package co.blastlab.serviceblbnavi.socket.info.server.file;

import co.blastlab.serviceblbnavi.socket.info.server.Info;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FileInfo extends Info {

	public FileInfo() {
		super(InfoType.FILE.getValue());
	}

	public enum FileInfoType {
		INFO(0),
		ASK_LIST(1),
		LIST(2),
		DELETE(3),
		DELETED(4),
		UPLOAD(5),
		ACK(6);

		private final int value;

		FileInfoType(final int newValue) {
			this.value = newValue;
		}

		private static final Map<Integer, FileInfoType> map = new HashMap<>();

		static {
			for (FileInfoType infoType : FileInfoType.values()) {
				map.put(infoType.getValue(), infoType);
			}
		}

		public int getValue() {
			return value;
		}

		public static FileInfoType from(int value) {
			return map.get(value);
		}
	}
}
