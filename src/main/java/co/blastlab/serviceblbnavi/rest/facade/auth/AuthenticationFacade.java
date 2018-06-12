package co.blastlab.serviceblbnavi.rest.facade.auth;

import co.blastlab.serviceblbnavi.dto.user.CredentialsDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.GenerateOperationID;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/auth")
@Api("/auth")
@GenerateOperationID
public interface AuthenticationFacade {
	@POST
	@ApiOperation(value = "authenticate", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "User not found"),
		@ApiResponse(code = 400, message = "Username or password incorrect")
	})
	Response authenticate(@ApiParam(value = "credentials", required = true) @Valid CredentialsDto credentials);

	@POST
	@ApiOperation(value = "logout", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "User not found")
	})
	@Path("/logout")
	@AuthorizedAccess
	Response logout();
}
