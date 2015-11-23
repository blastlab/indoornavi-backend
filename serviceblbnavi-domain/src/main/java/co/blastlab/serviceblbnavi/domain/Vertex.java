package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Vertex.FIND_ACTIVE_BY_FLOOR, query = "SELECT v FROM Vertex v WHERE v.floor.id = :floorId AND v.inactive = 0")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Vertex implements Serializable {

    public static final String FIND_ACTIVE_BY_FLOOR = "Vertex.findActiveByFloor";
    
    @Id
    @GeneratedValue
    private Long id;

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
    @OneToMany(mappedBy = "vertex")
    private List<BuildingExit> buildingExits;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "target")
    private List<Edge> targetEdges;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "source")
    private List<Edge> sourceEdges;

    @OneToMany(mappedBy = "vertex", fetch = FetchType.EAGER)
    private List<Goal> goals;

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

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
