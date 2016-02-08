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
    @NamedQuery(name = Goal.FIND_BY_BUILDING, query = "SELECT g FROM Goal g WHERE g.floor.building.id = :buildingId"),
    @NamedQuery(name = Goal.FIND_BY_FLOOR, query = "SELECT g FROM Goal g WHERE g.floor.id = :floorId")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Goal extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_BUILDING = "Goal.findByBuilding";
    public static final String FIND_BY_FLOOR = "Goal.findByFloor";

    private String name;

    private Double x;

    private Double y;

    @Column(nullable = false)
    private Boolean inactive;

    @JsonIgnore
    @ManyToOne
    private Floor floor;

    @JsonView({View.External.class, View.GoalInternal.class})
    @OneToMany(mappedBy = "goal", cascade = CascadeType.REMOVE)
    private List<GoalSelection> goalSelections;

    @Transient
    private Long floorId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
