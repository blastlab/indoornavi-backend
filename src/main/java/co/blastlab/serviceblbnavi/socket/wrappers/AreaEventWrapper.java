package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.socket.area.AreaEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaEventWrapper extends MessageWrapper {

	private AreaEvent event;

	public AreaEventWrapper(AreaEvent event) {
		super(MessageType.EVENT);
		this.event = event;
	}
}
