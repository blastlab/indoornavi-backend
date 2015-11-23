package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/goal")
@Api("/goal")
public class GoalFacade {

    @EJB
    private GoalBean goalBean;

    @EJB
    private VertexBean vertexBean;

    @EJB
    private BuildingBean buildingBean;

    @POST
    @ApiOperation(value = "create goal", response = Goal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex id or building id emtpy or vertex or building doesn't exist")
    })
    @JsonView(View.GoalInternal.class)
    public Goal create(@ApiParam(value = "vertex", required = true) Goal goal) {
        if (goal.getVertexId() != null && goal.getBuildingId() != null) {
            Vertex vertex = vertexBean.find(goal.getVertexId());
            Building building = buildingBean.find(goal.getBuildingId());
            if (vertex != null) {
                goal.setVertex(vertex);
                goal.setBuilding(building);
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
        if (goal == null) {
            throw new EntityNotFoundException();
        }
        goalBean.delete(goal);
        return Response.ok().build();
    }

    @PUT
    @ApiOperation(value = "update goal", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "goal id or goal empty or doesn't exist")
    })
    public Goal update(@ApiParam(value = "goal", required = true) Goal goal) {
        if (goal.getId() != null) {
            Goal g = goalBean.find(goal.getId());
            if (g != null) {
                g.setName(goal.getName());
                goalBean.update(g);
                return g;
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @ApiOperation(value = "update goals", response = Goal.class)
    @JsonView(View.GoalInternal.class)
    public List<Goal> update(@ApiParam(value = "goal", required = true) List<Goal> goals) {
        goalBean.update(goals);
        return goals;
    }

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "find goals for specified vertex", response = Goal.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    public List<Goal> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long vertexId) {
        if (vertexId != null) {
            System.out.println("getting goals");
            return goalBean.findAll(vertexId);
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
            goalBean.deactivate(goal);
            return goal;
        }
        throw new EntityNotFoundException();
    }
}
