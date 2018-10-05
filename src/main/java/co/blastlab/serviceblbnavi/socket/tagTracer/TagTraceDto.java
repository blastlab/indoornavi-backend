package co.blastlab.serviceblbnavi.socket.tagTracer;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
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
