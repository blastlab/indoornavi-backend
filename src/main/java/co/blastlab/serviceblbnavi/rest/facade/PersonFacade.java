package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.dto.person.PersonRequestDto;
import co.blastlab.serviceblbnavi.dto.person.PersonResponseDto;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;


@Path("/person")
@Api("/person")
public interface PersonFacade {

    @POST
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "register", response = Person.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "person with given email exists")
    })
    PersonResponseDto register(@ApiParam(value = "person", required = true) PersonRequestDto person);


    @PUT
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "login", response = Person.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid login data")
    })
    PersonResponseDto login(@ApiParam(value = "person", required = true) PersonRequestDto person);

    @GET
    @JsonView(View.PersonInternal.class)
    @Path("/current")
    @ApiOperation(value = "find current user")
    PersonResponseDto get();
}
