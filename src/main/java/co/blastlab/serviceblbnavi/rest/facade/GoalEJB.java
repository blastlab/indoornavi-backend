package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;


public class GoalEJB implements GoalFacade {

    @Inject
    private GoalBean goalBean;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private FloorRepository floorRepository;


    public Goal create(Goal goal) {
        if (goal.getFloorId() != null) {
            Floor floor = floorRepository.findBy(goal.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                goal.setFloor(floor);
                goalBean.create(goal);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Goal goal = goalBean.find(id);
        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            goalBean.delete(goal);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public Goal updateName(Goal goal) {
        if (goal.getId() != null) {
            Goal g = goalBean.find(goal.getId());
            if (g != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        g.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                g.setInactive(true);
                goal.setFloor(g.getFloor());
                goal.setId(null);
                goalBean.update(g);
                goalBean.create(goal);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }
    

    public Goal updateCoordinates(Goal goal) {
        if (goal.getId() != null) {
            Goal g = goalBean.find(goal.getId());
            if (g != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        g.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                g.setX(goal.getX());
                g.setY(goal.getY());
                goalBean.update(g);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }


    public Goal deactivate(Long goalId) {
        Goal goal = goalBean.find(goalId);
        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            goalBean.deactivate(goal);
            return goal;
        }
        throw new EntityNotFoundException();
    }


    public List<Goal> findByBuilding(Long buildingId) {
        if (buildingId != null) {
            List<Goal> goals = goalBean.findAllByBuildingId(buildingId);
            if (goals.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        goals.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return goals;
        }
        throw new EntityNotFoundException();
    }


    public List<Goal> findByFloor(Long floorId) {
        if (floorId != null) {
            List<Goal> goals = goalBean.findAllByFloorId(floorId);
            if (goals.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        goals.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return goals;
        }
        throw new EntityNotFoundException();
    }
    

    public List<Goal> findActiveByFloor(Long floorId) {
        if (floorId != null) {
            List<Goal> goals = goalBean.findActiveByFloorId(floorId);
            if (goals.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        goals.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return goals;
        }
        throw new EntityNotFoundException();
    }
}
