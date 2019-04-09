package co.blastlab.indoornavi.dto.sink;

import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
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
