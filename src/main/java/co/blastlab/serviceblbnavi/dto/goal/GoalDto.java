package co.blastlab.serviceblbnavi.dto.goal;

import co.blastlab.serviceblbnavi.domain.Goal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Min(0)
    private Double x;

    @NotNull
    @Min(0)
    private Double y;

    private boolean inactive;

    @NotNull
    private Long floorId;

    private List<Long> goalSelectionsIds;
}
