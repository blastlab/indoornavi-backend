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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Waypoint extends CustomIdGenerationEntity implements Serializable, Updatable {

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
    private List<WaypointVisit> waypointVisits = new ArrayList<>();
}
