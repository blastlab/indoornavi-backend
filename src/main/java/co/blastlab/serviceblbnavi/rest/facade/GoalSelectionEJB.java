package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.GoalSelectionBean;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;
import co.blastlab.serviceblbnavi.dto.goal.GoalSelectionDto;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;


public class GoalSelectionEJB implements GoalSelectionFacade {

    @Inject
    private GoalSelectionBean goalSelectionBean;

    @Inject
    private GoalBean goalBean;
    

    public GoalSelectionDto create(GoalSelectionDto goalSelection) {
        if (goalSelection.getGoalId() != null && goalSelection.getDevice() != null
                && goalSelection.getTimestamp() != null) {
            Goal goal = goalBean.find(goalSelection.getGoalId());
            if (goal != null) {
                GoalSelection goalSelectionEntity = new GoalSelection();
                goalSelectionEntity.setX(goalSelection.getX());
                goalSelectionEntity.setY(goalSelection.getY());
                goalSelectionEntity.setFloorLevel(goalSelection.getFloorLevel());
                goalSelectionEntity.setDevice(goalSelection.getDevice());
                goalSelectionEntity.setCreationDateTimestamp(goalSelection.getTimestamp());
                goalSelectionEntity.setGoal(goal);
                goalSelectionBean.create(goalSelectionEntity);
                return new GoalSelectionDto(goalSelectionEntity);
            }
        }
        throw new EntityNotFoundException();
    }
}
