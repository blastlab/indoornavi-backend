package co.blastlab.serviceblbnavi.rest.facade.debug;

import co.blastlab.serviceblbnavi.domain.DebugReport;
import co.blastlab.serviceblbnavi.dto.debug.DebugFileName;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import co.blastlab.serviceblbnavi.socket.measures.DistanceMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.event.Observes;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/debug")
@Api("/debug")
@SetOperationId
public interface DebugFacade {
	@ApiOperation(value = "list of files", response = DebugReport.class, responseContainer = "list")
	@GET
	@AuthorizedAccess("DEBUG")
	List<DebugReport> list() throws IOException;

	@Path("/{id: \\d+}")
	@GET
	@Produces({"image/png", "image/jpeg"})
	@ApiOperation(value = "download the file", response = Response.class)
	@AuthorizedAccess("DEBUG")
	Response download(@PathParam("id") Long id);

	@Path("/{id: \\d+}")
	@ApiOperation(value = "start debug", response = Response.class)
	@POST
	@AuthorizedAccess("DEBUG")
	Response start(@PathParam("id") @NotNull Long sinkId) throws IOException;

	@ApiOperation(value = "stop debug", response = Response.class)
	@POST
	@AuthorizedAccess("DEBUG")
	Response stop(@ApiParam(value = "debugFileName", required = true) @Valid DebugFileName debugFileName) throws IOException;

	@Path("/{id: \\d+}")
	@DELETE
	@AuthorizedAccess("DEBUG")
	Response delete(@PathParam("id") Long id);

	void rawMeasureEndpoint(@Observes DistanceMessage distanceMessage) throws JsonProcessingException;

	void calculatedCoordinatesEndpoint(@Observes UwbCoordinatesDto coordinatesDto);
}
