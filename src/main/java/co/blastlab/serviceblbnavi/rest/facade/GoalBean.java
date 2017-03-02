package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.GoalRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.goal.GoalDto;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class GoalBean extends UpdaterBean<GoalDto, Goal> implements GoalFacade {

	@Inject
	private PermissionBean permissionBean;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private GoalRepository goalRepository;

	@Inject
	private BuildingRepository buildingRepository;

	public GoalDto create(GoalDto goal) {
		Floor floor = floorRepository.findBy(goal.getFloorId());
		if (floor != null) {
			permissionBean.checkPermission(floor, Permission.UPDATE);
			Goal goalEntity = new Goal();
			goalEntity.setInactive(goal.isInactive());
			goalEntity.setY(goal.getY());
			goalEntity.setX(goal.getX());
			goalEntity.setName(goal.getName());
			goalEntity.setFloor(floor);
			goalEntity = goalRepository.save(goalEntity);
			return new GoalDto(goalEntity);
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		Goal goal = goalRepository.findBy(id);
		if (goal != null) {
			permissionBean.checkPermission(goal, Permission.UPDATE);
			goalRepository.remove(goal);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public GoalDto updateName(GoalDto goal) {
		if (goal.getId() != null) {
			Goal goalEntity = goalRepository.findBy(goal.getId());
			if (goalEntity != null) {
				permissionBean.checkPermission(goalEntity, Permission.UPDATE);

				// TODO: WTF is this method doing?! why setInactive(true) on previously created Goal and then create new Goal?
				// method name is updateName!
				goalEntity.setInactive(true);
				Goal newGoalEntity = new Goal();
				newGoalEntity.setInactive(goal.isInactive());
				newGoalEntity.setY(goal.getY());
				newGoalEntity.setX(goal.getX());
				newGoalEntity.setName(goal.getName());
				newGoalEntity.setFloor(goalEntity.getFloor());
				goalEntity.setId(null);
				goalRepository.save(goalEntity);
				goalRepository.save(newGoalEntity);
				return goal;
			}
		}
		throw new EntityNotFoundException();
	}

	public GoalDto updateCoordinates(GoalDto goal) {
		return super.updateCoordinates(goal, goalRepository);
	}

	public GoalDto deactivate(Long goalId) {
		GoalDto goal = new GoalDto();
		goal.setId(goalId);
		return super.deactivate(goal, goalRepository);
	}

	public List<GoalDto> findByBuilding(Long buildingId) {
		if (buildingId != null) {
			Building building = buildingRepository.findBy(buildingId);
			List<Floor> floors = floorRepository.findByBuilding(building);
			List<GoalDto> goals = new ArrayList<>();
			floors.forEach((floor -> goals.addAll(findByFloor(floor.getId()))));
			return goals;
		}
		throw new EntityNotFoundException();
	}

	public List<GoalDto> findByFloor(Long floorId) {
		if (floorId != null) {
			Floor floor = floorRepository.findBy(floorId);
			List<Goal> goals = goalRepository.findByFloor(floor);
			if (goals.size() > 0) {
				permissionBean.checkPermission(goals.get(0), Permission.READ);
			}
			return convertToDtos(goals);
		}
		throw new EntityNotFoundException();
	}

	public List<GoalDto> findActiveByFloor(Long floorId) {
		if (floorId != null) {
			Floor floor = floorRepository.findBy(floorId);
			List<Goal> goals = goalRepository.findByFloorAndInactive(floor, false);
			if (goals.size() > 0) {
				permissionBean.checkPermission(goals.get(0), Permission.READ);
			}
			return convertToDtos(goals);
		}
		throw new EntityNotFoundException();
	}

	private List<GoalDto> convertToDtos(List<Goal> goals) {
		List<GoalDto> goalDtos = new ArrayList<>();
		goals.forEach((goal -> goalDtos.add(new GoalDto(goal))));
		return goalDtos;
	}
}
