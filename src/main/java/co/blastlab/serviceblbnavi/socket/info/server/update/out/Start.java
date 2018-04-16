package co.blastlab.serviceblbnavi.socket.info.server.update.out;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfo.UpdateInfoType;

@Getter
@Setter
public class Start extends InfoCode {
	@JsonProperty("did")
	private Integer shortId;
	private List<Integer> route;
	@JsonProperty("file")
	private String fileName;
	private byte AorB;

	public Start(Integer shortId, List<Integer> route, String fileName, byte AorB) {
		super(UpdateInfoType.START.getValue());
		this.shortId = shortId;
		this.route = route;
		this.fileName = fileName;
		this.AorB = AorB;
	}
}
