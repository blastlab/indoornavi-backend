package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.Info;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfo extends Info {

	public FileInfo() {
		super(InfoType.FILE.getValue());
	}
}
