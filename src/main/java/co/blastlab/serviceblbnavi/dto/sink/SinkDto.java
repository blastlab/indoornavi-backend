package co.blastlab.serviceblbnavi.dto.sink;

import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SinkDto extends AnchorDto {
	private List<AnchorDto> anchors = new ArrayList<>();
	private Boolean configured;

	public SinkDto(Sink sink) {
		super(sink);
		this.anchors.addAll(sink.getAnchors().stream().map(AnchorDto::new).collect(Collectors.toList()));
		this.configured = sink.isConfigured();
	}
}
