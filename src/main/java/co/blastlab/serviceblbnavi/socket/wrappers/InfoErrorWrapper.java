package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.socket.info.controller.DeviceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class InfoErrorWrapper extends MessageWrapper {

	private String code;
	private DeviceStatus deviceStatus;

	public InfoErrorWrapper(String code) {
		super(MessageType.INFO_ERROR);
		this.code = code;
	}

	public InfoErrorWrapper(String code, DeviceStatus deviceStatus) {
		super(MessageType.INFO_ERROR);
		this.code = code;
		this.deviceStatus = deviceStatus;
	}
}
