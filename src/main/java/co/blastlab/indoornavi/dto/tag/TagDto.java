package co.blastlab.indoornavi.dto.tag;

import co.blastlab.indoornavi.domain.Tag;
import co.blastlab.indoornavi.dto.uwb.UwbDto;
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
