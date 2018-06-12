package co.blastlab.serviceblbnavi.rest.facade.sink;

import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.GenerateOperationID;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/sinks")
@Api("/sinks")
@GenerateOperationID
public interface SinkFacade {
	@GET
	@ApiOperation(value = "find all sinks", response = SinkDto.class, responseContainer = "List")
	@AuthorizedAccess("SINK_READ")
	List<SinkDto> findAll();

	@POST
	@ApiOperation(value = "create sink", response = SinkDto.class)
	@AuthorizedAccess("SINK_CREATE")
	SinkDto create(@ApiParam(value = "sink", required = true) @Valid SinkDto sink);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update sink", response = SinkDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Sink id empty or sink does not exist")
	})
	@AuthorizedAccess("SINK_UPDATE")
	SinkDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
		@ApiParam(value = "sink", required = true) @Valid SinkDto sink);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete sink by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Sink id empty or sink does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("SINK_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);
}
