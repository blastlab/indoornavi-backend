package co.blastlab.indoornavi.socket.tagTracer;

import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TagTraceDto {
	private TagDto tag;
	private FloorDto floor;
}
