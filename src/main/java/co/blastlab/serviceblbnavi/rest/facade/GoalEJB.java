package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.goal.GoalDto;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


public class GoalEJB implements GoalFacade {

    @Inject
    private GoalBean goalBean;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private FloorBean floorBean;

    @Inject
    private FloorRepository floorRepository;


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
            goalBean.create(goalEntity);
            return new GoalDto(goalEntity);
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Goal goal = goalBean.find(id);
        if (goal != null) {
            permissionBean.checkPermission(goal, Permission.UPDATE);
            goalBean.delete(goal);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public GoalDto updateName(GoalDto goal) {
        if (goal.getId() != null) {
            Goal goalEntity = goalBean.find(goal.getId());
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
                goalBean.update(goalEntity);
                goalBean.create(newGoalEntity);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }
    

    public GoalDto updateCoordinates(GoalDto goal) {
        if (goal.getId() != null) {
            Goal goalEntity = goalBean.find(goal.getId());
            if (goalEntity != null) {
                permissionBean.checkPermission(goalEntity, Permission.UPDATE);
                goalEntity.setX(goal.getX());
                goalEntity.setY(goal.getY());
                goalBean.update(goalEntity);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }


    public GoalDto deactivate(Long goalId) {
        Goal goalEntity = goalBean.find(goalId);
        if (goalEntity != null) {
            permissionBean.checkPermission(goalEntity, Permission.UPDATE);
            goalBean.deactivate(goalEntity);
            return new GoalDto(goalEntity);
        }
        throw new EntityNotFoundException();
    }


    public List<GoalDto> findByBuilding(Long buildingId) {
        if (buildingId != null) {
            List<Goal> goals = goalBean.findAllByBuildingId(buildingId);
            if (goals.size() > 0) {
                permissionBean.checkPermission(goals.get(0), Permission.READ);
            }
            return convertToDtos(goals);
        }
        throw new EntityNotFoundException();
    }


    public List<GoalDto> findByFloor(Long floorId) {
        if (floorId != null) {
            List<Goal> goals = goalBean.findAllByFloorId(floorId);
            if (goals.size() > 0) {
                permissionBean.checkPermission(goals.get(0), Permission.READ);
            }
            return convertToDtos(goals);
        }
        throw new EntityNotFoundException();
    }
    

    public List<GoalDto> findActiveByFloor(Long floorId) {
        if (floorId != null) {
            List<Goal> goals = goalBean.findActiveByFloorId(floorId);
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
