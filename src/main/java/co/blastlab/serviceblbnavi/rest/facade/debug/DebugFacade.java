package co.blastlab.serviceblbnavi.rest.facade.debug;

import co.blastlab.serviceblbnavi.domain.DebugReport;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import co.blastlab.serviceblbnavi.socket.measures.DistanceMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.enterprise.event.Observes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

	@Path("/start")
	@ApiOperation(value = "start debug", response = Response.class)
	@POST
	@AuthorizedAccess("DEBUG")
	Response start() throws IOException;

	@Path("/stop")
	@ApiOperation(value = "stop debug", response = Response.class)
	@POST
	@AuthorizedAccess("DEBUG")
	Response stop() throws IOException;

	void rawMeasureEndpoint(@Observes DistanceMessage distanceMessage) throws JsonProcessingException;

	void calculatedCoordinatesEndpoint(@Observes UwbCoordinatesDto coordinatesDto);
}
