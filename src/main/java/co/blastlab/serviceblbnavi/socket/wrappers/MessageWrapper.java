package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MessageWrapper {

	private MessageType type;
}
