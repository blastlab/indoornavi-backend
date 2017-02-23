package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.GoalRepository;
import co.blastlab.serviceblbnavi.dao.repository.GoalSelectionRepository;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

@Stateless
public class GoalSelectionEJB implements GoalSelectionFacade {

    @Inject
    private GoalSelectionRepository goalSelectionRepository;

    @Inject
    private GoalRepository goalRepository;


    public GoalSelection create(GoalSelection goalSelection) {
        if (goalSelection.getGoalId() != null && goalSelection.getDevice() != null
                && (goalSelection.getCreationDateTimestamp() != null || goalSelection.getTimestamp() != null)) {
            if (goalSelection.getTimestamp() != null) {
                goalSelection.setCreationDateTimestamp(goalSelection.getTimestamp());
                goalSelection.setTimestamp(null);
            }
            Goal goal = goalRepository.findBy(goalSelection.getGoalId());
            if (goal != null) {
                goalSelection.setGoal(goal);
                goalSelectionRepository.save(goalSelection);
                return goalSelection;
            }
        }
        throw new EntityNotFoundException();
    }
}
