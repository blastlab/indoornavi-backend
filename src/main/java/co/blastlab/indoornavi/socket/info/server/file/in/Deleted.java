package co.blastlab.indoornavi.socket.info.server.file.in;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Deleted extends InfoCode {
	private Boolean success;
}
