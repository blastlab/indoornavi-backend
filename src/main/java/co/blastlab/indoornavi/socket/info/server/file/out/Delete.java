package co.blastlab.indoornavi.socket.info.server.file.out;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
import co.blastlab.indoornavi.socket.info.server.file.FileInfo.FileInfoType;
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
