package co.blastlab.serviceblbnavi.dto.area;

import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AreaConfigurationDto {
	private Long id;
	private List<TagDto> tags = new ArrayList<>();
	private Integer offset;
	private AreaConfiguration.Mode mode;

	public AreaConfigurationDto(AreaConfiguration areaConfiguration) {
		this.setId(areaConfiguration.getId());
		this.setMode(areaConfiguration.getMode());
		this.setOffset(areaConfiguration.getOffset());
		this.setTags(areaConfiguration.getTags().stream().map(TagDto::new).collect(Collectors.toList()));
	}
}
