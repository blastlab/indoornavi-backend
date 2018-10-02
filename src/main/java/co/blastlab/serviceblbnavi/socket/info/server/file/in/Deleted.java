package co.blastlab.serviceblbnavi.socket.info.server.file.in;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Deleted extends InfoCode {
	private Boolean success;
}
