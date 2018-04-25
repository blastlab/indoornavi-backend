package co.blastlab.serviceblbnavi.socket.info.server.broadcast;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeviceConnected {
	private Integer shortId;
	private Long longId;
	private Integer hardwareMajor;
	private Integer firmwareMajor;
	private Integer firmwareMinor;
	private List<Integer> route = new ArrayList<>();
}
