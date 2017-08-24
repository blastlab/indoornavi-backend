package co.blastlab.serviceblbnavi.dto.map;

import co.blastlab.serviceblbnavi.domain.Map;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.dto.user.UserDto;
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
	private FloorDto floor;
	private List<UserDto> users = new ArrayList<>();
	private List<TagDto> tags = new ArrayList<>();

	public MapDto(Map map) {
		this.id = map.getId();
		this.floor = new FloorDto(map.getFloor());
		this.users = map.getUsers().stream().map(UserDto::new).collect(Collectors.toList());
		this.tags = map.getTags().stream().map(TagDto::new).collect(Collectors.toList());
	}
}
