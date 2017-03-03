package co.blastlab.serviceblbnavi.dto.floor;

import co.blastlab.serviceblbnavi.domain.Floor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FloorDto {

	public FloorDto(Floor floor) {
		this.setId(floor.getId());
		this.setLevel(floor.getLevel());
		this.setBitmapHeight(floor.getBitmapHeight());
		this.setBitmapWidth(floor.getBitmapWidth());
		this.setStartZoom(floor.getStartZoom());
		this.setBuildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null);
		floor.getBeacons().forEach((beacon -> this.getBeaconsIds().add(beacon.getId())));
		floor.getGoals().forEach((goal -> this.getGoalsIds().add(goal.getId())));
		floor.getVertices().forEach((vertex -> this.getVerticesIds().add(vertex.getId())));
		floor.getWaypoints().forEach((waypoint -> this.getWaypointsIds().add(waypoint.getId())));
	}

	// TODO: on every field that is in some metric, should be description metric it uses i.e. bitmapWidth should be described: px

	@ApiModelProperty(example = "1")
	private Long id;

	@NotNull
	@ApiModelProperty(example = "1")
	private Integer level;

	@ApiModelProperty(hidden = true)
	private Integer bitmapWidth;

	@ApiModelProperty(hidden = true)
	private Integer bitmapHeight;

	@ApiModelProperty(hidden = true)
	private Double startZoom;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long buildingId;

	@ApiModelProperty(hidden = true)
	private List<Long> waypointsIds = new ArrayList<>();

	@ApiModelProperty(hidden = true)
	private List<Long> verticesIds = new ArrayList<>();

	@ApiModelProperty(hidden = true)
	private List<Long> goalsIds = new ArrayList<>();

	@ApiModelProperty(hidden = true)
	private List<Long> beaconsIds = new ArrayList<>();

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Extended extends FloorDto {

		@NotNull
		@Min(0)
		// TODO: this field can have more friendly name like: scale?
		@JsonProperty("mToPix")
		private Double mToPix;

		public Extended(Floor floor) {
			super(floor);
			this.setMToPix(floor.getMToPix());
		}
	}
}
