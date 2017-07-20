package co.blastlab.serviceblbnavi.dto.configuration;

import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConfigurationDto {
	private Long floorId;
	private Integer version;
	private List<AnchorDto> anchors = new ArrayList<>();
	private ScaleDto scale;
}
