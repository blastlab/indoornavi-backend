package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.socket.info.command.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CommandSinkWrapper extends MessageWrapper {

	private String name;
	private InternetAddress ipAndPort;

	public CommandSinkWrapper(String name, InternetAddress ipAndPort) {
		super(MessageType.COMMAND_SINK_CONNECTED);
		this.name = name;
		this.ipAndPort = ipAndPort;
	}
}
