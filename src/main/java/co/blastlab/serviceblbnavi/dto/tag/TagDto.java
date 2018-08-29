package co.blastlab.serviceblbnavi.dto.tag;

import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.uwb.UwbDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class TagDto extends UwbDto {
	public TagDto(Tag tag) {
		super(tag);
	}
}