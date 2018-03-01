package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.InfoCode;
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
