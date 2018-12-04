package co.blastlab.serviceblbnavi.socket.info.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {
	private CommandType type;
	private Object args;
}
