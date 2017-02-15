package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
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
    Person register(@ApiParam(value = "person", required = true) Person person);


    @PUT
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "register", response = Person.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid login data")
    })
    Person login(@ApiParam(value = "person", required = true) Person person);

    @GET
    @JsonView(View.PersonInternal.class)
    @Path("/current")
    @ApiOperation(value = "find current user")
    @TokenAuthorization
    Person get();
}
