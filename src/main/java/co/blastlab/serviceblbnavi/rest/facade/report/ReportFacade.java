package co.blastlab.serviceblbnavi.rest.facade.report;

import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
import co.blastlab.serviceblbnavi.dto.report.ReportFilterDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/reports")
@Api("/report")
public interface ReportFacade {
	@POST
	@Path("/coordinates")
	@ApiOperation(value = "get coordinates", response = CoordinatesDto.class)
	@AuthorizedAccess
	List<CoordinatesDto> getCoordinates(@ApiParam(value = "filter", required = true) @Valid ReportFilterDto filter);
}
