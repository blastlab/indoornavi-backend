package co.blastlab.serviceblbnavi.socket.info.server.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeviceConnected {
	@JsonProperty("did")
	private Integer shortId;
	@JsonProperty("eui")
	private Long longId;
	@JsonProperty("hMajor")
	private Integer hardwareMajor;
	@JsonProperty("fMajor")
	private Integer firmwareMajor;
	@JsonProperty("fMinor")
	private Integer firmwareMinor;
	private List<Integer> route = new ArrayList<>();
}
