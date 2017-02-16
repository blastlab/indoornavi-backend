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
public class Vertex extends CustomIdGenerationEntity implements Serializable, Updatable {

    public static final String FIND_ACTIVE_BY_FLOOR = "Vertex.findActiveByFloor";

    private Double x;

    private Double y;

    @Column(nullable = false)
    private Boolean inactive;

    @Transient
    private boolean isFloorDownChangeable;

    @Transient
    private boolean isFloorUpChangeable;

    @Transient
    private Long floorId;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "vertex", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<BuildingExit> buildingExits;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "target", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Edge> targetEdges;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "source", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Edge> sourceEdges;

    @JsonIgnore
    @ManyToOne
    private Floor floor;

    @OneToOne(mappedBy = "vertex", fetch = FetchType.EAGER)
    @JsonIgnore
    private VertexFloorChangeabilityView vertexFloorChangeabilityView;

    @PrePersist
    void prePersist() {
        if (inactive == null) {
            inactive = false;
        }
    }

    @PostLoad
    public void onLoad() {
        isFloorDownChangeable = vertexFloorChangeabilityView.getIsFloorDownChangeable();
        isFloorUpChangeable = vertexFloorChangeabilityView.getIsFloorUpChangeable();
    }

    @Override
    public String toString() {
        return "Vertex{" + "id=" + getId() + ", x=" + x + ", y=" + y
                + ", inactive=" + inactive
                + ", isFloorDownChangeable=" + isFloorDownChangeable
                + ", isFloorUpChangeable=" + isFloorUpChangeable
                + ", floorId=" + floorId
                + ", buildingExits=" + buildingExits
                + ", targetEdges=" + targetEdges
                + ", sourceEdges=" + sourceEdges
                + ", floor=" + floor
                + ", vertexFloorChangeabilityView=" + vertexFloorChangeabilityView
                + '}';
    }

}
