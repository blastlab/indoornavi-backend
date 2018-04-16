package co.blastlab.serviceblbnavi.socket.info.server.update.in;

import co.blastlab.serviceblbnavi.socket.info.server.update.UpdateInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAcknowledge extends UpdateInfo {
	@JsonProperty("did")
	private Integer shortId;
	@JsonProperty("ack_cnt")
	private Short count;
	private Short toward;
}
