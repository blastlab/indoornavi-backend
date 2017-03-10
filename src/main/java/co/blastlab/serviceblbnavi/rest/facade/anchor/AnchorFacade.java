package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/anchor")
@Api("/anchor")
public interface AnchorFacade {

	@POST
	@ApiOperation(value = "create", response = Anchor.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "anchor id or complex empty or doesn't exist")
	})
	Anchor create(@ApiParam(value = "anchor", required = true) @Valid Anchor anchor);
}
