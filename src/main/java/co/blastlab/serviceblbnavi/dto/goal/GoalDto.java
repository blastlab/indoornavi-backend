package co.blastlab.serviceblbnavi.dto.goal;

import co.blastlab.serviceblbnavi.domain.Goal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GoalDto {
    public GoalDto(Goal goal) {
        this.setId(goal.getId());
        this.setName(goal.getName());
        this.setX(goal.getX());
        this.setY(goal.getY());
        this.setFloorId(goal.getFloor() != null ? goal.getFloor().getId() : null);
        this.setInactive(goal.isInactive());
        goal.getGoalSelections().forEach((goalSelection -> this.getGoalSelectionsIds().add(goalSelection.getId())));
    }

    private Long id;

    private String name;

    private Double x;

    private Double y;

    private boolean inactive;

    private Long floorId;

    private List<Long> goalSelectionsIds;
}
