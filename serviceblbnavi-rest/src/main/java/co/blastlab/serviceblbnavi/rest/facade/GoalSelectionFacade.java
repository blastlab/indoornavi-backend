package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.GoalBean;
import co.blastlab.serviceblbnavi.dao.GoalSelectionBean;
import co.blastlab.serviceblbnavi.domain.Goal;
import co.blastlab.serviceblbnavi.domain.GoalSelection;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/goalSelection")
@Api("/goalSelection")
public class GoalSelectionFacade {

    @EJB
    private GoalSelectionBean goalSelectionBean;

    @EJB
    private GoalBean goalBean;
    
    @POST
    @ApiOperation(value = "create goal selection", response = GoalSelection.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "invalid goal selection\'s data")
    })
    public GoalSelection create(@ApiParam(value = "goalSelection", required = true) GoalSelection goalSelection) {
        if (goalSelection.getGoalId() != null && goalSelection.getDevice() != null
                && (goalSelection.getCreationDateTimestamp() != null || goalSelection.getTimestamp() != null)) {
            if (goalSelection.getTimestamp() != null) {
                goalSelection.setCreationDateTimestamp(goalSelection.getTimestamp());
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
