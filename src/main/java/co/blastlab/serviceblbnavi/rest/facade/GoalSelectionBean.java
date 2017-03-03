package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.GoalRepository;
import co.blastlab.serviceblbnavi.dao.repository.GoalSelectionProductionRepository;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;
import co.blastlab.serviceblbnavi.dto.goal.GoalSelectionDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

@Stateless
public class GoalSelectionBean implements GoalSelectionFacade {

	@Inject
	private GoalSelectionProductionRepository goalSelectionProductionRepository;

	@Inject
	private GoalRepository goalRepository;

	public GoalSelectionDto create(GoalSelectionDto goalSelection) {
		Goal goal = goalRepository.findBy(goalSelection.getGoalId());
		if (goal != null) {
			GoalSelection goalSelectionEntity = new GoalSelection();
			goalSelectionEntity.setX(goalSelection.getX());
			goalSelectionEntity.setY(goalSelection.getY());
			goalSelectionEntity.setFloorLevel(goalSelection.getFloorLevel());
			goalSelectionEntity.setDevice(goalSelection.getDevice());
			goalSelectionEntity.setCreationDateTimestamp(goalSelection.getTimestamp());
			goalSelectionEntity.setGoal(goal);
			goalSelectionProductionRepository.save(goalSelectionEntity);
			return new GoalSelectionDto(goalSelectionEntity);
		}
		throw new EntityNotFoundException();
	}
}
