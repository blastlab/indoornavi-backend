package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Waypoint.FIND_BY_BUILDING_ID, query = "SELECT w FROM Waypoint w WHERE w.floor.building.id = :buildingId"),
    @NamedQuery(name = Waypoint.FIND_ACTIVE_BY_FLOOR_ID, query = "SELECT w FROM Waypoint w WHERE w.floor.id = :floorId AND w.inactive = false")
})
public class Waypoint extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_BUILDING_ID = "Waypoint.findByBuildingId";
    public static final String FIND_ACTIVE_BY_FLOOR_ID = "Waypoint.findActiveByFloorId";

    private Double x;

    private Double y;

    private Integer timeToCheckout;

    private Double distance;

    private String details;

    private boolean inactive;

    private String name;

    @ManyToOne
    @JoinColumn(updatable = false)
    private Floor floor;

    @OneToMany(mappedBy = "waypoint", cascade = CascadeType.REMOVE)
    private List<WaypointVisit> waypointVisits;
}
