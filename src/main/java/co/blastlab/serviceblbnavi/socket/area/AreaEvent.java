package co.blastlab.serviceblbnavi.socket.area;

import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaEvent {
	private AreaConfiguration.Mode mode;
	private String areaName;
	private Integer tagId;
}
