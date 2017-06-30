package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dto.user.ChangePasswordDto;
import co.blastlab.serviceblbnavi.dto.user.UserDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Api("/users")
public interface UserFacade {

	@GET
	@ApiOperation(value = "get list of users", response = UserDto.class, responseContainer = "list")
	@AuthorizedAccess("USER_READ")
	List<UserDto> getAll();

	@POST
	@ApiOperation(value = "create new user", response = UserDto.class)
	@AuthorizedAccess("USER_CREATE")
	UserDto create(@ApiParam(value = "user", required = true) @Valid UserDto userDto);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update the user", response = UserDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "User id is empty or user does not exist")
	})
	@AuthorizedAccess("USER_UPDATE")
	UserDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	               @ApiParam(value = "user", required = true) @Valid UserDto userDto);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete the user", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "User id is empty or user does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully and there is no new information to return"),
		@ApiResponse(code = 403, message = "You can not delete superUser")
	})
	@AuthorizedAccess("USER_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@PUT
	@Path("/changePassword")
	@ApiOperation(value = "change your password", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 304, message = "Old password doesn't match"),
		@ApiResponse(code = 204, message = "Password successfully changed and there is no new information to return")
	})
	@AuthorizedAccess
	Response changePassword(ChangePasswordDto changePasswordDto);
}
