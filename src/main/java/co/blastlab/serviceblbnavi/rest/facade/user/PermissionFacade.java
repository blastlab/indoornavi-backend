package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dto.user.PermissionDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/permissions")
@Api("/permissions")
public interface PermissionFacade {
	@GET
	@ApiOperation(value = "get list of permissions", response = PermissionDto.class, responseContainer = "list")
	@AuthorizedAccess("PERMISSION_GROUP_READ")
	List<PermissionDto> getAll();
}
