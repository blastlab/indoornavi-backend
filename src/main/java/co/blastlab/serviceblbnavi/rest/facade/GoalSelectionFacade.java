package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.goal.GoalSelectionDto;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/goalSelection")
@Api("/goalSelection")
@Produces("application/json")
public interface GoalSelectionFacade {

	@POST
	@ApiOperation(value = "create goal selection", response = GoalSelectionDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "invalid goal selection\'s data")
	})
	@JsonView(View.GoalSelectionInternal.class)
	GoalSelectionDto create(@ApiParam(value = "goalSelection", required = true) @Valid GoalSelectionDto goalSelection);
}
