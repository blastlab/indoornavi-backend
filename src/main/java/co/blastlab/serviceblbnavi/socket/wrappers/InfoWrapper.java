package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.socket.info.controller.DeviceStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class InfoWrapper extends MessageWrapper {

	private Set<DeviceStatus> devices;

	public InfoWrapper(Set<DeviceStatus> devices) {
		super(MessageType.INFO);
		this.devices = devices;
	}
}
