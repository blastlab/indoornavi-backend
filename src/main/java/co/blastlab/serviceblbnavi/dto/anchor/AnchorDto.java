package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AnchorDto {

	public AnchorDto(Anchor anchor) {
		this.shortId = anchor.getShortId();
		this.longId = anchor.getLongId();
		this.x = anchor.getX();
		this.y = anchor.getY();
		this.floorId = (anchor.getFloor() == null) ? null : anchor.getFloor().getId();
		this.name = anchor.getName();
		this.id = anchor.getId();
	}

	@ApiModelProperty(example = "1", readOnly = true)
	private Long id;

	@NotNull
	@ApiModelProperty(example = "14733")
	private int shortId;

	@NotNull
	@ApiModelProperty(example = "87367834748864600")
	private long longId;

	@NotNull
	private Double x;

	@NotNull
	private Double y;

	@ApiModelProperty(example = "1")
	private Long floorId;

	@ApiModelProperty(example = "Name")
	private String name;
}
