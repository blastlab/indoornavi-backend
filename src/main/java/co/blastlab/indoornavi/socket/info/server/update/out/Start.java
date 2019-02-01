package co.blastlab.indoornavi.socket.info.server.update.out;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static co.blastlab.indoornavi.socket.info.server.update.UpdateInfo.UpdateInfoType;

@Getter
@Setter
public class Start extends InfoCode {
	private Integer shortId;
	private List<Integer> route;
	private String fileName;
	private Integer partition;

	public Start(Integer shortId, List<Integer> route, String fileName, Integer partition) {
		super(UpdateInfoType.START.getValue());
		this.shortId = shortId;
		this.route = route;
		this.fileName = fileName;
		this.partition = partition;
	}
}
