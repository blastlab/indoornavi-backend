package co.blastlab.serviceblbnavi.socket.command.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommandRequestBase {
	private CommandType type;
}
