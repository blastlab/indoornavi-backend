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
	}

	@NotNull
	@ApiModelProperty(example = "14733")
	private short shortId;

	@NotNull
	@ApiModelProperty(example = "8736783474886474673")
	private long longId;

	@NotNull
	private Double x;

	@NotNull
	private Double y;

	@ApiModelProperty(example = "1")
	private Long floorId;

	@Getter
	@Setter
	@NoArgsConstructor
	@ApiModel(value = "AnchorId")
	public static class WithId extends AnchorDto {

		@ApiModelProperty(example = "1")
		private Long id;

		public WithId(Anchor anchor) {
			super(anchor);
			this.setId(anchor.getId());
		}
	}
}
