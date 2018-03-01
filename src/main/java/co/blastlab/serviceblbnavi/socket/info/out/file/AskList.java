package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.Info;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskList extends Info {
	public String extension;

	public AskList(String extension) {
		super(FileArgsType.ASK_LIST.getValue());
		this.extension = extension;
	}
}
