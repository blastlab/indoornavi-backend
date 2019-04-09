package co.blastlab.indoornavi.socket.info.server.file.in;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAcknowledge extends InfoCode {
	private String file;
	private Integer offset;
	private Integer dataSize;
}
