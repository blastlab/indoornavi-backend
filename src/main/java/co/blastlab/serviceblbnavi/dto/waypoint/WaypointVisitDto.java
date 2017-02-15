package co.blastlab.serviceblbnavi.dto.waypoint;

import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String device;

    private Date timestamp;

    private Long waypointId;
}
