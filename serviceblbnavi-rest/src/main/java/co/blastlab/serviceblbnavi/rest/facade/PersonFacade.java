package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * @author Michał Koszałka
 */
@Path("/person")
@Api("/person")
//@Stateless
public interface PersonFacade {

 /*   @EJB
    private PersonBean personBean;

    @Inject
    private PersonRepository personRepository;
*/
    /*@Inject
    private AuthorizationBean authorizationBean;*/

    @POST
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "register", response = Person.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "person with given email exists")
    })
    public Person register(@ApiParam(value = "person", required = true) Person person);

    @PUT
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "register", response = Person.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "invalid login data")
    })
    public Person login(@ApiParam(value = "person", required = true) Person person);

    @GET
    @JsonView(View.PersonInternal.class)
    @Path("/current")
    @ApiOperation(value = "find current user")
    public Person get();

}
