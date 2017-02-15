package co.blastlab.serviceblbnavi.dto.waypoint;

import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WaypointDto {
    public WaypointDto(Waypoint waypoint) {
        this.setId(waypoint.getId());
        this.setX(waypoint.getX());
        this.setY(waypoint.getY());
        this.setName(waypoint.getName());
        this.setInactive(waypoint.isInactive());
        this.setDetails(waypoint.getDetails());
        this.setTimeToCheckout(waypoint.getTimeToCheckout());
        this.setDistance(waypoint.getDistance());
        this.setFloorId(waypoint.getFloor() != null ? waypoint.getFloor().getId() : null);
        waypoint.getWaypointVisits().forEach((waypointVisit -> this.getWaypointVisitsIds().add(waypointVisit.getId())));
    }

    private Long id;

    private Double x;

    private Double y;

    private Integer timeToCheckout;

    private Double distance;

    private String details;

    private boolean inactive;

    private String name;

    private Long floorId;

    @JsonView({View.WaypointInternal.class})
    private List<Long> waypointVisitsIds;
}
