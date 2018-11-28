package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

import static co.blastlab.serviceblbnavi.socket.wrappers.MessageType.SERVER_COMMAND;

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
