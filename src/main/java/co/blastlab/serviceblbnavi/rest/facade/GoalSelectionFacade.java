package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.GoalSelectionBean;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/goalSelection")
@Api("/goalSelection")
@Produces("application/json")
public class GoalSelectionFacade {

    @Inject
    private GoalSelectionBean goalSelectionBean;

    @Inject
    private GoalBean goalBean;
    
    @POST
    @ApiOperation(value = "create goal selection", response = GoalSelection.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "invalid goal selection\'s data")
    })
    @JsonView(View.GoalSelectionInternal.class)
    public GoalSelection create(@ApiParam(value = "goalSelection", required = true) GoalSelection goalSelection) {
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
