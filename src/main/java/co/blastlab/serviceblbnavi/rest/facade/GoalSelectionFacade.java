package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.GoalSelection;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.*;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/goalSelection")
@Api("/goalSelection")
@Produces("application/json")
public interface GoalSelectionFacade {

    @POST
    @ApiOperation(value = "create goal selection", response = GoalSelection.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid goal selection\'s data")
    })
    @JsonView(View.GoalSelectionInternal.class)
    public GoalSelection create(@ApiParam(value = "goalSelection", required = true) GoalSelection goalSelection);
}
