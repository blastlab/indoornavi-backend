package co.blastlab.serviceblbnavi.socket.info.command.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommandRequestBase {
	private CommandType type;
	private Object args;
}
