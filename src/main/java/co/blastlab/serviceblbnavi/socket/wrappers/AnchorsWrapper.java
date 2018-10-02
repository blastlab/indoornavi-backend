package co.blastlab.serviceblbnavi.socket.wrappers;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class AnchorsWrapper extends MessageWrapper {

	List<AnchorDto> anchors = new ArrayList<>();

	public AnchorsWrapper(List<AnchorDto> anchors) {
		super(MessageType.ANCHORS);
		this.anchors.addAll(anchors);
	}
}
