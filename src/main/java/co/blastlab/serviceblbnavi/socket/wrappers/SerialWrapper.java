package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static co.blastlab.serviceblbnavi.socket.wrappers.MessageType.SERIAL;

@Getter
@Setter
@ToString(callSuper = true)
public class SerialWrapper extends MessageWrapper {
	private String serial;

	public SerialWrapper(String serial) {
		super(SERIAL);
		this.serial = serial;
	}
}
