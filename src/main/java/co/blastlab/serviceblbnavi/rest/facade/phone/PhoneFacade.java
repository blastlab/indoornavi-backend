package co.blastlab.serviceblbnavi.rest.facade.phone;

import co.blastlab.serviceblbnavi.dto.phone.PhoneDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/phones")
@Api("/phones")
@SetOperationId
public interface PhoneFacade {
	@POST
	@ApiOperation(value = "register or authenticate phone", response = PhoneDto.class)
	@AuthorizedAccess
	PhoneDto auth(@ApiParam(value = "phone", required = true) @Valid PhoneDto phone);
}
