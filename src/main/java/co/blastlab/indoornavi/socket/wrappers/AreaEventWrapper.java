package co.blastlab.indoornavi.socket.wrappers;

import co.blastlab.indoornavi.socket.area.AreaEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class AreaEventWrapper extends MessageWrapper {

	private AreaEvent event;

	public AreaEventWrapper(AreaEvent event) {
		super(MessageType.EVENT);
		this.event = event;
	}
}
