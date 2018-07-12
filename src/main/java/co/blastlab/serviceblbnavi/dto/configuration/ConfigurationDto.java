package co.blastlab.serviceblbnavi.dto.configuration;

import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ConfigurationDto {
	private Long id;
	private Long floorId;
	private Integer version;
	private Data data;
	private Date publishedDate;
	private Date savedDraftDate;

	public ConfigurationDto(Configuration configuration) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		this.setId(configuration.getId());
		this.setVersion(configuration.getVersion());
		this.setFloorId(configuration.getFloor().getId());
		this.setData(objectMapper.readValue(configuration.getData(), ConfigurationDto.Data.class));
		this.setSavedDraftDate(configuration.getSaveDraftDate());
		this.setPublishedDate(configuration.getPublishedDate());
	}

	@Getter
	@Setter
	@ToString
	public static class Data {
		private List<SinkDto> sinks = new ArrayList<>();
		private List<AnchorDto> anchors = new ArrayList<>();
		private ScaleDto scale;
		private List<AreaDto> areas = new ArrayList<>();
	}
}
