package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoErrorWrapper extends MessageWrapper {

	private String code;

	public InfoErrorWrapper(String code) {
		super(MessageType.INFO_ERROR);
		this.code = code;
	}
}
