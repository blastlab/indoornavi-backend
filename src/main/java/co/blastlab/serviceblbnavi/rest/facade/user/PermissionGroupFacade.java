package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dto.user.PermissionGroupDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.GenerateOperationID;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/permissionGroups")
@Api("/permissionGroups")
@GenerateOperationID
public interface PermissionGroupFacade {
	@GET
	@ApiOperation(value = "get list of permission groups", response = PermissionGroupDto.class, responseContainer = "list")
	@AuthorizedAccess("PERMISSION_GROUP_READ")
	List<PermissionGroupDto> getAll();

	@POST
	@ApiOperation(value = "create permission group", response = PermissionGroupDto.class)
	@AuthorizedAccess("PERMISSION_GROUP_CREATE")
	PermissionGroupDto create(@ApiParam(value = "permissionGroup", required = true) @Valid PermissionGroupDto permissionGroup);

	@PUT
	@ApiOperation(value = "update permission group", response = PermissionGroupDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Permission group id empty or Permission group does not exist"),
	})
	@Path("/{id: \\d+}")
	@AuthorizedAccess("PERMISSION_GROUP_UPDATE")
	PermissionGroupDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                          @ApiParam(value = "permissionGroup", required = true) @Valid PermissionGroupDto permissionGroup);

	@DELETE
	@ApiOperation(value = "delete permission group", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Permission group id empty or Permission group does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@Path("/{id: \\d+}")
	@AuthorizedAccess("PERMISSION_GROUP_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);
}
