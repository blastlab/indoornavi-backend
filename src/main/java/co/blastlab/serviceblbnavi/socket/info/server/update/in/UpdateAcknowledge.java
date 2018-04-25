package co.blastlab.serviceblbnavi.socket.info.server.update.in;

import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAcknowledge extends UpdateInfo {
	private Integer shortId;
	private Short count;
	private Short toward;
}
