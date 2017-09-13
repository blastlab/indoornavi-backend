package co.blastlab.serviceblbnavi.dto.configuration;

import co.blastlab.serviceblbnavi.domain.Configuration;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConfigurationDto {
	private Long floorId;
	private Integer version;
	private Boolean published;
	private Data data;

	public ConfigurationDto(Configuration configuration) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		this.setVersion(configuration.getVersion());
		this.setFloorId(configuration.getFloor().getId());
		this.setData(objectMapper.readValue(configuration.getData(), ConfigurationDto.Data.class));
		this.setPublished(configuration.getPublished());
	}

	@Getter
	@Setter
	public static class Data {
		private List<SinkDto> sinks = new ArrayList<>();
		private ScaleDto scale;
	}
}
