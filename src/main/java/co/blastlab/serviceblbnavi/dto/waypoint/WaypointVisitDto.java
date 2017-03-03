package co.blastlab.serviceblbnavi.dto.waypoint;

import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class WaypointVisitDto {

	public WaypointVisitDto(WaypointVisit waypointVisit) {
		this.setDevice(waypointVisit.getDevice());
		this.setTimestamp(waypointVisit.getCreationDateTimestamp());
		this.setWaypointId(waypointVisit.getWaypoint() != null ? waypointVisit.getWaypoint().getId() : null);
	}

	@NotNull
	private String device;

	// TODO: any other restrictions here? like it can't be in a future or in a past
	@NotNull
	private Date timestamp;

	@NotNull
	@ApiModelProperty(example = "1")
	private Long waypointId;
}
