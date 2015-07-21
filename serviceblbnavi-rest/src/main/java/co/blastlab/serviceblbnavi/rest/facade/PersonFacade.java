package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Michał Koszałka
 */
@Path("/person")
@Api("/person")
public class PersonFacade {

	@EJB
	private PersonBean personBean;

	@Inject
	private AuthorizationBean authorizationBean;

	@POST
	@ApiOperation(value = "register", response = Person.class)
	@ApiResponses({
		@ApiResponse(code = 409, message = "person with given email exists")
	})
	public Person register(@ApiParam(value = "person", required = true) Person person) {
		if (personBean.findByEmail(person.getEmail()) != null) {
			throw new EntityExistsException();
		}
		person.generateAuthToken();
		personBean.create(person);
		return person;
	}

	@GET
	@Path("/current")
	@ApiOperation(value = "find current user")
	public Person get() {
		return authorizationBean.getCurrentUser();
	}

}
