package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    @NamedQuery(name = Goal.FIND_BY_VERTEX, query = "SELECT g FROM Goal g WHERE g.vertex.id = :vertexId"),
    @NamedQuery(name = Goal.FIND_BY_BUILDING, query = "SELECT g FROM Goal g WHERE g.building.id = :buildingId"),
    @NamedQuery(name = Goal.FIND_BY_FLOOR, query = "SELECT g FROM Goal g WHERE g.vertex.floor.id = :floorId")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Goal extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_VERTEX = "Goal.findByVertex";
    public static final String FIND_BY_BUILDING = "Goal.findByBuilding";
    public static final String FIND_BY_FLOOR = "Goal.findByFloor";

    private String name;

    @Column(nullable = false)
    private Boolean inactive;

    @JsonIgnore
    @ManyToOne
    private Building building;

    @JsonIgnore
    @ManyToOne
    private Vertex vertex;

    @JsonView({View.External.class, View.GoalInternal.class})
    @OneToMany(mappedBy = "goal", cascade = CascadeType.REMOVE)
    private List<GoalSelection> goalSelections;

    @Transient
    private Long buildingId;

    @Transient
    private Long vertexId;

    @PrePersist
    void prePersist() {
        if (inactive == null) {
            inactive = false;
        }
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getVertexId() {
        return vertexId;
    }

    public void setVertexId(Long vertexId) {
        this.vertexId = vertexId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public List<GoalSelection> getGoalSelections() {
        return goalSelections;
    }

    public void setGoalSelections(List<GoalSelection> goalSelections) {
        this.goalSelections = goalSelections;
    }

}
