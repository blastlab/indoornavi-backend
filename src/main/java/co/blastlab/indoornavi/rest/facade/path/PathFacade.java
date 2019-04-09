package co.blastlab.indoornavi.rest.facade.path;

import co.blastlab.indoornavi.dto.path.PathDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/paths")
@Api("/paths")
@SetOperationId
public interface PathFacade {
	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "get paths for specific floor", response = PathDto.class, responseContainer = "List")
	@AuthorizedAccess
	List<PathDto> getPaths(@PathParam("id") @Valid Long floorId);
}
