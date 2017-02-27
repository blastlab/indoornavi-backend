package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.person.PersonRequestDto;
import co.blastlab.serviceblbnavi.dto.person.PersonResponseDto;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/person")
@Api("/person")
public interface PersonFacade {

	@POST
	@ApiOperation(value = "register", response = PersonResponseDto.class)
	@ApiResponses({
		@ApiResponse(code = 409, message = "person with given email exists")
	})
	PersonResponseDto register(@ApiParam(value = "person", required = true) @Valid PersonRequestDto person);

	@PUT
	@ApiOperation(value = "login", response = PersonResponseDto.class)
	@ApiResponses({
		@ApiResponse(code = 401, message = "invalid login data")
	})
	PersonResponseDto login(@ApiParam(value = "person", required = true) @Valid PersonRequestDto person);

	@GET
	@Path("/current")
	@ApiOperation(value = "find current user")
	@TokenAuthorization
	PersonResponseDto get();
}
