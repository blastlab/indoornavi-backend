package co.blastlab.serviceblbnavi.socket.dto;

import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TagsWrapper extends MessageWrapper {

	private List<TagDto> tags = new ArrayList<>();

	public TagsWrapper(List<TagDto> tags) {
		super(MessageType.TAGS);
		this.tags.addAll(tags);
	}
}
