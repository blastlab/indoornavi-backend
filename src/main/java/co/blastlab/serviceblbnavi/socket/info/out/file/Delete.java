package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.InfoCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Delete extends InfoCode {
	private String path;

	public Delete(String path) {
		super(FileInfoType.DELETE.getValue());
		this.path = path;
	}
}
