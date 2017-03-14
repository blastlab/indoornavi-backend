package co.blastlab.serviceblbnavi.dto.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
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
		this.id = anchor.getId();
		this.shortId = anchor.getShortId();
		this.longId = anchor.getLongId();
		this.x = anchor.getX();
		this.y = anchor.getY();
		if (anchor.getFloor() != null) {
			this.floorId = anchor.getFloor().getId();
		}
	}

	@NotNull
	@ApiModelProperty(example = "1")
	private long id;

	@NotEmpty
	private String shortId;

	@NotEmpty
	private String longId;

	@NotNull
	private Double x;

	@NotNull
	private Double y;

	@ApiModelProperty(example = "1")
	private Long floorId;

	/*@Getter
	@Setter
	@NoArgsConstructor
	public static class NewFloor extends AnchorDto {

		@ApiModelProperty(example = "1")
		private Long floorId;

		public NewFloor(Anchor anchor) {
			super(anchor);
			this.setFloorId(anchor.getFloor() != null ? anchor.getFloor().getId() : null);
		}
	}*/
}
