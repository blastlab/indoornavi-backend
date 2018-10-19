package co.blastlab.serviceblbnavi.socket.info.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InternetAddress {
	private String ip;
	private int port;
}
