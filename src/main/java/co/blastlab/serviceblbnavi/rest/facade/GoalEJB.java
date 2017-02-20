package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.GoalRepository;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class GoalEJB extends UpdaterBean<Goal> implements GoalFacade {

    @Inject
    private GoalRepository goalRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private BuildingRepository buildingRepository;

    public Goal create(Goal goal) {
        return this.create(goal, goalRepository);
    }


    public Response delete(Long id) {
        Goal goal = goalRepository.findBy(id);
        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            goalRepository.remove(goal);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public Goal updateName(Goal goal) {
        if (goal.getId() != null) {
            Goal g = goalRepository.findBy(goal.getId());
            if (g != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        g.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                g.setInactive(true);
                goal.setFloor(g.getFloor());
                goal.setId(null);
                goalRepository.save(g);
                goalRepository.save(goal);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }


    public Goal updateCoordinates(Goal goal) {
        return this.updateCoordinates(goal, goalRepository);
    }


    public Goal deactivate(Long goalId) {
        return this.deactivate(goalId, goalRepository);
    }

    public List<Goal> findByBuilding(Long buildingId) {
        if (buildingId != null) {
            Building building = buildingRepository.findBy(buildingId);
            List<Floor> floors = floorRepository.findByBuilding(building);
            List<Goal> goals = new ArrayList<>();
            floors.stream().forEach((floor) -> {
                goals.addAll(goalRepository.findByFloor(floor));
            });
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
            Floor floor = floorRepository.findBy(floorId);
            List<Goal> goals = goalRepository.findByFloor(floor);
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
            Floor floor = floorRepository.findBy(floorId);
            List<Goal> goals = goalRepository.findByFloorAndInactive(floor, false);
            if (goals.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        goals.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return goals;
        }
        throw new EntityNotFoundException();
    }
}
