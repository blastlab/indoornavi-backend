package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Waypoint extends CustomIdGenerationEntity implements Serializable, Updatable {

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
}
