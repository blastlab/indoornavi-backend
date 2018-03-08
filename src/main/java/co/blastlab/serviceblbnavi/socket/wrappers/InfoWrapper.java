package co.blastlab.serviceblbnavi.socket.wrappers;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InfoWrapper extends MessageWrapper {

	private List<Integer> devicesIds;

	public InfoWrapper(List<Integer> devicesIds) {
		super(MessageType.INFO);
		this.devicesIds = devicesIds;
	}
}
