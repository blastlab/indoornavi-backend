package co.blastlab.indoornavi.dto.report;

import co.blastlab.indoornavi.ext.deserializer.JsonDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReportFilterDto {
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private LocalDateTime from;
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private LocalDateTime to;
	private Long floorId;
	private List<Long> tagsIds = new ArrayList<>();
}
