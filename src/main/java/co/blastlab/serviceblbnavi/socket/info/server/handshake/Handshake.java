package co.blastlab.serviceblbnavi.socket.info.server.handshake;

import co.blastlab.serviceblbnavi.socket.info.server.Info;
import lombok.Getter;

@Getter
public class Handshake extends Info {

	public Handshake() {
		super(InfoType.COMMAND.getValue());
		setArgs("version");
	}

	public Handshake(Integer shortId) {
		super(InfoType.COMMAND.getValue());
		setArgs(String.format("version did:%s", Integer.toHexString(shortId)));
	}
}
