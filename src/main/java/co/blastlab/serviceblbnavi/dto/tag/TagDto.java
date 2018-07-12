package co.blastlab.serviceblbnavi.dto.tag;

import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class TagDto extends DeviceDto {
	public TagDto(Long id, Integer shortId, Long longId, String name, Long floorId, Boolean verified, String firmwareVersion) {
		super(id, shortId, longId, name, floorId, verified, firmwareVersion);
	}

	public TagDto(Tag tag) {
		super(tag);
	}
}