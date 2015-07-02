package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.domain.Person;
import javax.ejb.EJB;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Michał Koszałka
 */
@Path("/person")
public class PersonFacade {

	@EJB
	private PersonBean personBean;

	@POST
	public Person register(Person person) {
		System.out.println(person.getEmail());
		if (personBean.getByEmail(person.getEmail()) != null) {
			throw new EntityExistsException();
		}
		personBean.create(person);
		return person;
	}
	
	@GET
	@Path("/{id: \\d+}")
	public Person get(@PathParam("id") Long id) {
		Person person = personBean.get(id);
		if(person == null) {
			throw new EntityNotFoundException();
		}
		return person;
	}

}
