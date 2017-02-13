package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.GoalSelectionBean;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;


public class GoalSelectionEJB implements GoalSelectionFacade {

    @Inject
    private GoalSelectionBean goalSelectionBean;

    @Inject
    private GoalBean goalBean;
    

    public GoalSelection create(GoalSelection goalSelection) {
        if (goalSelection.getGoalId() != null && goalSelection.getDevice() != null
                && (goalSelection.getCreationDateTimestamp() != null || goalSelection.getTimestamp() != null)) {
            if (goalSelection.getTimestamp() != null) {
                goalSelection.setCreationDateTimestamp(goalSelection.getTimestamp());
                goalSelection.setTimestamp(null);
            }
            Goal goal = goalBean.find(goalSelection.getGoalId());
            if (goal != null) {
                goalSelection.setGoal(goal);
                goalSelectionBean.create(goalSelection);
                return goalSelection;
            }
        }
        throw new EntityNotFoundException();
    }
}
