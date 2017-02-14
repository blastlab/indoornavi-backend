package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.GoalRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class GoalEJB implements GoalFacade {

/*    @Inject
    private GoalBean goalBean;*/

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
        if (goal.getFloorId() != null) {
            Floor floor = floorRepository.findBy(goal.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                goal.setFloor(floor);
                goalRepository.save(goal);
                return goal;
            }
        }
        throw new EntityNotFoundException();
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
            //Goal g = goalBean.find(goal.getId());
            Goal g = goalRepository.findBy(goal.getId());
            if (g != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        g.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                g.setInactive(true);
                goal.setFloor(g.getFloor());
                goal.setId(null);
                //goalBean.update(g);
                //goalBean.create(goal);
                goalRepository.save(g);
                goalRepository.save(goal);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }
    

    public Goal updateCoordinates(Goal goal) {
        if (goal.getId() != null) {
            //Goal g = goalBean.find(goal.getId());
            Goal g = goalRepository.findBy(goal.getId());
            if (g != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        g.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                g.setX(goal.getX());
                g.setY(goal.getY());
                //goalBean.update(g);
                goalRepository.save(g);
                return goal;
            }
        }
        throw new EntityNotFoundException();
    }


    public Goal deactivate(Long goalId) {
        //Goal goal = goalBean.find(goalId);
        Goal goal = goalRepository.findBy(goalId);

        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            deactivate(goal);
            return goal;
        }
        throw new EntityNotFoundException();
    }

    public void deactivate(Goal goal) {
        goal.setInactive(true);
        goalRepository.save(goal);
    }

    public List<Goal> findByBuilding(Long buildingId) {
        if (buildingId != null) {
            //List<Goal> goals = goalBean.findAllByBuildingId(buildingId);
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
            //List<Goal> goals = goalBean.findAllByFloorId(floorId);
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
            //List<Goal> goals = goalBean.findActiveByFloorId(floorId);
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
