package co.blastlab.indoornavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandErrorWrapper extends MessageWrapper {

	private String code;
	private Integer shortId;

	public CommandErrorWrapper(String code) {
		super(MessageType.COMMAND_ERROR);
		this.code = code;
	}

	public CommandErrorWrapper(String code, Integer shortId) {
		super(MessageType.COMMAND_ERROR);
		this.code = code;
		this.shortId = shortId;
	}
}
