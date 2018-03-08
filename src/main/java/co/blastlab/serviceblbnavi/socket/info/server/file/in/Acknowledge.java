package co.blastlab.serviceblbnavi.socket.info.server.file.in;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Acknowledge extends InfoCode {
	private String file;
	private Integer offset;
	@JsonProperty("dsize")
	private Integer batchSize;
}
