package co.blastlab.indoornavi.rest.facade.publication;

import co.blastlab.indoornavi.dto.map.OriginChecker;
import co.blastlab.indoornavi.dto.map.PublicationDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/publications")
@Api("/publications")
@SetOperationId
public interface PublicationFacade {

	@POST
	@ApiOperation(value = "create new publication", response = PublicationDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("PUBLICATION_CREATE")
	PublicationDto create(@ApiParam(value = "publication", required = true) @Valid PublicationDto publication);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "edit publication", response = PublicationDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	@AuthorizedAccess("PUBLICATION_UPDATE")
	PublicationDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                      @ApiParam(value = "publication", required = true) @Valid PublicationDto publication);

	@GET
	@ApiOperation(value = "get all publications", response = PublicationDto.class, responseContainer = "list")
	@AuthorizedAccess("PUBLICATION_READ")
	List<PublicationDto> getAll();

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "remove specific publication", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Publication with given id does not exist")
	})
	@AuthorizedAccess("PUBLICATION_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@POST
	@Path("/checkOrigin")
	@AuthorizedAccess
	Boolean checkOrigin(OriginChecker originChecker);

	@GET
	@Path("/{id: \\d+}/tags")
	@AuthorizedAccess("PUBLICATION_READ")
	List<TagDto> getTagsForUser(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long floorId);

}
