package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandErrorWrapper extends MessageWrapper {

	private String code;

	public CommandErrorWrapper(String code) {
		super(MessageType.COMMAND_ERROR);
		this.code = code;
	}
}
