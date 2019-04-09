package co.blastlab.indoornavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

import static co.blastlab.indoornavi.socket.wrappers.MessageType.SERVER_COMMAND;

@Getter
@Setter
public class ServerCommandWrapper extends MessageWrapper {

	private String value;
	private Integer sinkShortId;

	public ServerCommandWrapper(String value, Integer sinkShortId) {
		super(SERVER_COMMAND);
		this.value = value;
		this.sinkShortId = sinkShortId;
	}
}
