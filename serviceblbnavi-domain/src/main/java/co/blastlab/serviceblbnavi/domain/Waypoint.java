package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Waypoint.FIND_BY_BUILDING_ID, query = "SELECT w FROM Waypoint w WHERE w.floor.building.id = :buildingId"),
    @NamedQuery(name = Waypoint.FIND_ACTIVE_BY_FLOOR_ID, query = "SELECT w FROM Waypoint w WHERE w.floor.id = :floorId AND w.inactive = false")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Waypoint extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_BUILDING_ID = "Waypoint.findByBuildingId";
    public static final String FIND_ACTIVE_BY_FLOOR_ID = "Waypoint.findActiveByFloorId";

    private Double x;

    private Double y;

    private Integer timeToCheckout;

    private Double distance;

    private String details;

    private Boolean inactive;

    private String name;

    @Transient
    private Long floorId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(updatable = false)
    private Floor floor;

    @JsonView({View.External.class, View.WaypointInternal.class})
    @OneToMany(mappedBy = "waypoint", cascade = CascadeType.REMOVE)
    private List<WaypointVisit> waypointVisits;

    @PrePersist
    void prePersist() {
        if (inactive == null) {
            inactive = false;
        }
    }
    
    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getTimeToCheckout() {
        return timeToCheckout;
    }

    public void setTimeToCheckout(Integer timeToCheckout) {
        this.timeToCheckout = timeToCheckout;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public List<WaypointVisit> getWaypointVisits() {
        return waypointVisits;
    }

    public void setWaypointVisits(List<WaypointVisit> waypointVisits) {
        this.waypointVisits = waypointVisits;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
