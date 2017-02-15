package co.blastlab.serviceblbnavi.dto.goal;

import co.blastlab.serviceblbnavi.domain.GoalSelection;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class GoalSelectionDto {
    public GoalSelectionDto(GoalSelection goalSelection) {
        this.setId(goalSelection.getId());
        this.setDevice(goalSelection.getDevice());
        this.setX(goalSelection.getX());
        this.setY(goalSelection.getY());
        this.setFloorLevel(goalSelection.getFloorLevel());
        this.setTimestamp(goalSelection.getCreationDateTimestamp());
        this.setGoalId(goalSelection.getGoal() != null ? goalSelection.getGoal().getId() : null);
    }

    private Long id;

    private String device;

    private Double x;

    private Double y;

    private Integer floorLevel;

    private Date timestamp;

    private Long goalId;

    // TODO: Check why is it here
    @JsonGetter("X")
    public Double getXCapitalized() {
        return x;
    }

    @JsonSetter("X")
    public void setXCapitalized(Double x) {
        this.x = x;
    }

    @JsonGetter("Y")
    public Double getYCapitalized() {
        return y;
    }

    @JsonSetter("Y")
    public void setYCapitalized(Double y) {
        this.y = y;
    }
}
