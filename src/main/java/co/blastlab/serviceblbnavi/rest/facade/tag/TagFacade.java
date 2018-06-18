package co.blastlab.serviceblbnavi.rest.facade.tag;

import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tags")
@Api("/tags")
@SetOperationId
public interface TagFacade {

	@POST
	@ApiOperation(value = "create tag", response = TagDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("TAG_CREATE")
	TagDto create(@ApiParam(value = "tag", required = true) @Valid TagDto tag);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update tag by id", response = TagDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor or tag with given id does not exist")
	})
	@AuthorizedAccess("TAG_UPDATE")
	TagDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                 @ApiParam(value = "tag", required = true) @Valid TagDto tag);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete tag by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Tag id empty or tag doesn not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("TAG_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all tags", response = TagDto.class, responseContainer = "List")
	@AuthorizedAccess("TAG_READ")
	List<TagDto> findAll();
}
