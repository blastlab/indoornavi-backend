package co.blastlab.indoornavi.socket.wrappers;

import co.blastlab.indoornavi.socket.info.controller.DeviceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
public class InfoWrapper extends MessageWrapper {

	private Set<DeviceStatus> devices;

	public InfoWrapper(Set<DeviceStatus> devices) {
		super(MessageType.INFO);
		this.devices = devices;
	}
}
