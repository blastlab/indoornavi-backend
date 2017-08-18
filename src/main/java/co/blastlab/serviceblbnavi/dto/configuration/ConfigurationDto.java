package co.blastlab.serviceblbnavi.dto.configuration;

import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConfigurationDto {
	private Long floorId;
	private Integer version;
	private Data data;

	@Getter
	@Setter
	public static class Data {
		private List<SinkDto> sinks = new ArrayList<>();
		private ScaleDto scale;
	}
}
