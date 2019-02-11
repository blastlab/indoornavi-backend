package co.blastlab.indoornavi.rest.facade.phone;

import co.blastlab.indoornavi.dto.phone.PhoneCoordinatesDto;
import co.blastlab.indoornavi.dto.phone.PhoneDto;
import co.blastlab.indoornavi.ext.filter.AuthorizedAccess;
import co.blastlab.indoornavi.ext.filter.SetOperationId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/phones")
@Api("/phones")
@SetOperationId
public interface PhoneFacade {
	@POST
	@Path("/auth")
	@ApiOperation(value = "register or authenticate phone", response = PhoneDto.class)
	@AuthorizedAccess
	PhoneDto auth(@ApiParam(value = "phone", required = true) @Valid PhoneDto phone);

	@POST
	@Path("/coordinates")
	@ApiOperation(value = "save coordiantes to database", response = PhoneCoordinatesDto.class, responseContainer = "List")
	List<PhoneCoordinatesDto> saveCoordinates(@ApiParam(value = "coordinatesList", required = true) @Valid List<PhoneCoordinatesDto> coordinatesList);
}
