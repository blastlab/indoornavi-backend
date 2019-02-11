package co.blastlab.indoornavi.socket.wrappers;

import co.blastlab.indoornavi.dto.tag.TagDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class TagsWrapper extends MessageWrapper {

	private List<TagDto> tags = new ArrayList<>();

	public TagsWrapper(List<TagDto> tags) {
		super(MessageType.TAGS);
		this.tags.addAll(tags);
	}
}
