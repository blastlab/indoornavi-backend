package co.blastlab.serviceblbnavi.dto.map;

import co.blastlab.serviceblbnavi.domain.Publication;
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
public class PublicationDto {
	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;
	private List<FloorDto> floors = new ArrayList<>();
	private List<UserDto> users = new ArrayList<>();
	private List<TagDto> tags = new ArrayList<>();

	public PublicationDto(Publication map) {
		this.id = map.getId();
		this.floors = map.getFloors().stream().map(FloorDto::new).collect(Collectors.toList());
		this.users = map.getUsers().stream().map(UserDto::new).collect(Collectors.toList());
		this.tags = map.getTags().stream().map(TagDto::new).collect(Collectors.toList());
	}
}
