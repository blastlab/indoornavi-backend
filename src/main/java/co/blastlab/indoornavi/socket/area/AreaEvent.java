package co.blastlab.indoornavi.socket.area;

import co.blastlab.indoornavi.domain.AreaConfiguration;
import co.blastlab.indoornavi.ext.serializer.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AreaEvent {
	private AreaConfiguration.Mode mode;
	private Long areaId;
	private String areaName;
	private Integer tagId;
	@JsonSerialize(using = JsonDateSerializer.class)
	private LocalDateTime date;
}
