package co.blastlab.serviceblbnavi.rest.facade.publication;

import co.blastlab.serviceblbnavi.dto.map.OriginChecker;
import co.blastlab.serviceblbnavi.dto.map.PublicationDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/publications")
@Api("/publications")
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
	@ApiOperation(value = "get specific publication", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Map with given id does not exist")
	})
	@AuthorizedAccess("PUBLICATION_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@POST
	@Path("/checkOrigin")
	@AuthorizedAccess
	Boolean checkOrigin(OriginChecker originChecker);

	@GET
	@Path("/{id: \\d+}/getTags")
	@AuthorizedAccess("PUBLICATION_READ")
	List<TagDto> getTagsForUser(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long floorId);

}
