package co.blastlab.serviceblbnavi.socket.area;

import co.blastlab.serviceblbnavi.domain.AreaConfigurationMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaEvent {
	private AreaConfigurationMode mode;
	private String areaName;
	private Integer tagId;
}
