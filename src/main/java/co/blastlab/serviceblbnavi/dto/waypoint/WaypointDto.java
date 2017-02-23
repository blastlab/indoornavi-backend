package co.blastlab.serviceblbnavi.dto.waypoint;

import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WaypointDto implements Updatable<WaypointDto, Waypoint> {
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

    @NotNull
    @Min(0)
    private Double x;

    @NotNull
    @Min(0)
    private Double y;

    @NotNull
    @Min(0)
    private Integer timeToCheckout;

    @NotNull
    @Min(0)
    private Double distance;

    private String details;

    private boolean inactive;

    @NotNull
    private String name;

    @NotNull
    private Long floorId;

    @JsonView({View.WaypointInternal.class})
    private List<Long> waypointVisitsIds;

    @Override
    public WaypointDto create(Waypoint entity) {
        return new WaypointDto(entity);
    }
}
