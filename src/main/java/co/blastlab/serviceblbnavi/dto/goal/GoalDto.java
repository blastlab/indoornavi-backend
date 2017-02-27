package co.blastlab.serviceblbnavi.dto.goal;

import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import com.wordnik.swagger.annotations.ApiModelProperty;
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
public class GoalDto implements Updatable<GoalDto, Goal> {

	public GoalDto(Goal goal) {
		this.setId(goal.getId());
		this.setName(goal.getName());
		this.setX(goal.getX());
		this.setY(goal.getY());
		this.setFloorId(goal.getFloor() != null ? goal.getFloor().getId() : null);
		this.setInactive(goal.isInactive());
		goal.getGoalSelections().forEach((goalSelection -> this.getGoalSelectionsIds().add(goalSelection.getId())));
	}

	@ApiModelProperty(example = "1")
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
	@ApiModelProperty(example = "2")
	private Long floorId;

	@ApiModelProperty(hidden = true)
	private List<Long> goalSelectionsIds;

	@Override
	public GoalDto create(Goal entity) {
		return new GoalDto(entity);
	}
}
