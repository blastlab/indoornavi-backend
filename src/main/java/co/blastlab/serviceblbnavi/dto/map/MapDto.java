package co.blastlab.serviceblbnavi.dto.map;

import co.blastlab.serviceblbnavi.domain.Map;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MapDto {
	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;
	@ApiModelProperty(example = "1")
	private Long floorId;
	private List<Long> users = new ArrayList<>();
	private List<Long> tags = new ArrayList<>();

	public MapDto(Map map) {
		this.id = map.getId();
		this.floorId = map.getFloor() != null ? map.getFloor().getId() : null;
		this.users = map.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList());
		this.tags = map.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toList());
	}
}
