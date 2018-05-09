package co.blastlab.serviceblbnavi.socket.info.server.file.out;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo.FileInfoType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskList extends InfoCode {
	public String extension;

	public AskList(String extension) {
		super(FileInfoType.ASK_LIST.getValue());
		this.extension = extension;
	}
}
