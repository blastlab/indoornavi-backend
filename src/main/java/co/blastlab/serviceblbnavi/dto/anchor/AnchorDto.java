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
		this.shortId = anchor.getShortId();
		this.longId = anchor.getLongId();
		this.x = anchor.getX();
		this.y = anchor.getY();
	}

	@NotEmpty
	@ApiModelProperty(example = "5F19")
	private String shortId;

	@NotEmpty
	@ApiModelProperty(example = "1")
	private String longId;

	@NotNull
	private Double x;

	@NotNull
	private Double y;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithFloor extends AnchorDto {

		@ApiModelProperty(example = "1")
		private Long floorId;

		public WithFloor(Anchor anchor) {
			super(anchor);
			this.setFloorId(anchor.getFloor() != null ? anchor.getFloor().getId() : null);
		}

		@Getter
		@Setter
		@NoArgsConstructor
		public static class WithId extends WithFloor {

			@ApiModelProperty(example = "1")
			private Long id;

			public WithId(Anchor anchor) {
				super(anchor);
				this.setId(anchor.getId());
			}
		}
	}
}
