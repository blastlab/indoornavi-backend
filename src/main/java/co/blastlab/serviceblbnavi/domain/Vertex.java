package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Vertex.FIND_ACTIVE_BY_FLOOR, query = "SELECT v FROM Vertex v WHERE v.floor.id = :floorId AND v.inactive = false")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Vertex extends CustomIdGenerationEntity implements Serializable {

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

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public List<BuildingExit> getBuildingExits() {
        return buildingExits;
    }

    public void setBuildingExits(List<BuildingExit> buildingExits) {
        this.buildingExits = buildingExits;
    }

    public List<Edge> getTargetEdges() {
        return targetEdges;
    }

    public void setTargetEdges(List<Edge> targetEdges) {
        this.targetEdges = targetEdges;
    }

    public List<Edge> getSourceEdges() {
        return sourceEdges;
    }

    public void setSourceEdges(List<Edge> sourceEdges) {
        this.sourceEdges = sourceEdges;
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

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public boolean isIsFloorDownChangeable() {
        return isFloorDownChangeable;
    }

    public void setIsFloorDownChangeable(boolean isFloorDownChangeable) {
        this.isFloorDownChangeable = isFloorDownChangeable;
    }

    public boolean isIsFloorUpChangeable() {
        return isFloorUpChangeable;
    }

    public void setIsFloorUpChangeable(boolean isFloorUpChangeable) {
        this.isFloorUpChangeable = isFloorUpChangeable;
    }

    public VertexFloorChangeabilityView getVertexFloorChangeabilityView() {
        return vertexFloorChangeabilityView;
    }

    public void setVertexFloorChangeabilityView(VertexFloorChangeabilityView vertexFloorChangeabilityView) {
        this.vertexFloorChangeabilityView = vertexFloorChangeabilityView;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

}
