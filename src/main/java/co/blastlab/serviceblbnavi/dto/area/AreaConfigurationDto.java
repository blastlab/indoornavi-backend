package co.blastlab.serviceblbnavi.dto.area;

import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Device;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AreaConfigurationDto {
	private Long id;
	private List<Integer> tags = new ArrayList<>();
	private Integer offset;
	private AreaConfiguration.Mode mode;

	public AreaConfigurationDto(AreaConfiguration areaConfiguration) {
		this.setId(areaConfiguration.getId());
		this.setMode(areaConfiguration.getMode());
		this.setOffset(areaConfiguration.getOffset());
		this.setTags(areaConfiguration.getTags().stream().map(Device::getShortId).collect(Collectors.toList()));
	}
}
