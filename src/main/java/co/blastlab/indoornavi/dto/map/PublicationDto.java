package co.blastlab.indoornavi.dto.map;

import co.blastlab.indoornavi.domain.Publication;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.dto.user.UserDto;
import io.swagger.annotations.ApiModelProperty;
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
