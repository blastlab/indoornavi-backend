package co.blastlab.serviceblbnavi.socket.measures;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
class DistanceMessageWrapper {
	private List<DistanceMessage> measures;
	private List<Info> info;
}
