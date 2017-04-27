package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
class TagsWrapper extends MessageDto {

	private List<TagDto> tags;

	TagsWrapper(TypeMessage type, List<TagDto> tags) {
		super(type);
		this.tags = tags;
	}
}
