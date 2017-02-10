package co.blastlab.serviceblbnavi.domain;

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
@NamedQueries({
    @NamedQuery(name = Goal.FIND_BY_BUILDING, query = "SELECT g FROM Goal g WHERE g.floor.building.id = :buildingId"),
    @NamedQuery(name = Goal.FIND_BY_FLOOR, query = "SELECT g FROM Goal g WHERE g.floor.id = :floorId"),
    @NamedQuery(name = Goal.FIND_ACTIVE_BY_FLOOR, query = "SELECT g FROM Goal g WHERE g.floor.id = :floorId AND g.inactive = false")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Goal extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_BUILDING = "Goal.findByBuilding";
    public static final String FIND_BY_FLOOR = "Goal.findByFloor";
    public static final String FIND_ACTIVE_BY_FLOOR = "Goal.findActiveByFloor";
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

}