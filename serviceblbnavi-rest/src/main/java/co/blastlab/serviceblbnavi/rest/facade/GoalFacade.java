package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/goal")
@Api("/goal")
public class GoalFacade {

    @Inject
    private GoalBean goalBean;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private FloorBean floorBean;

    @Inject
    private FloorRepository floorRepository;

    @POST
    @ApiOperation(value = "create goal", response = Goal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor id emtpy or floor doesn't exist")
    })
    @JsonView(View.GoalInternal.class)
    public Goal create(@ApiParam(value = "goal", required = true) Goal goal) {
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

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete goal", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "goal with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Goal goal = goalBean.find(id);
        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            goalBean.delete(goal);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/name")
    @ApiOperation(value = "update goal name", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    public Goal updateName(@ApiParam(value = "goal", required = true) Goal goal) {
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
    
    @PUT
    @Path("/coordinates")
    @ApiOperation(value = "update goal coordinates", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    public Goal updateCoordinates(@ApiParam(value = "goal", required = true) Goal goal) {
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

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "deactivates goal of given id", response = Goal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    public Goal deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long goalId) {
        Goal goal = goalBean.find(goalId);
        if (goal != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    goal.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            goalBean.deactivate(goal);
            return goal;
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/building/{id: \\d+}")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified building", response = Goal.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building with given id wasn't found")
    })
    public List<Goal> findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId) {
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

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified floor", response = Goal.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Goal> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId) {
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
    
    @GET
    @Path("/floor/{id: \\d+}/active")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified floor", response = Goal.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Goal> findActiveByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId) {
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
