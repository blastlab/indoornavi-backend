package co.blastlab.indoornavi.rest.facade.report;

import co.blastlab.indoornavi.dto.report.ReportFilterDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import co.blastlab.indoornavi.socket.area.AreaEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/reports")
@Api("/reports")
@SetOperationId
public interface ReportFacade {
	@POST
	@Path("/coordinates")
	@ApiOperation(value = "get coordinates", response = UwbCoordinatesDto.class, responseContainer = "List")
	@AuthorizedAccess
	List<UwbCoordinatesDto> getCoordinates(@ApiParam(value = "filter", required = true) @Valid ReportFilterDto filter);

	@POST
	@Path("/events")
	@ApiOperation(value = "get area events", response = AreaEvent.class, responseContainer = "List")
	@AuthorizedAccess
	List<AreaEvent> getAreaEvents(@ApiParam(value = "filter", required = true) @Valid ReportFilterDto filter);

}
