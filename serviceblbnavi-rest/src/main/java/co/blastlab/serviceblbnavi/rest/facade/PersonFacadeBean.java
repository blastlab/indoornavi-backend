package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Stateless
public class PersonFacadeBean implements PersonFacade{

    @Inject
    PersonRepository personRepository;

    @Inject
    PersonBean personBean;

    public Person register(Person person) {
        Person p = personRepository.findOptionalByEmail(person.getEmail());
        if (p != null) {
            throw new EntityExistsException();
        }
        p = new Person(person.getEmail(), person.getPlainPassword());
        p.generateAuthToken();
        personRepository.save(p);
        return p;
    }

    public Person login( Person person) {
        Person p = personRepository.findOptionalByEmail(person.getEmail());
        if (p == null) {
            throw new EntityNotFoundException();
        }
        personBean.checkPassword(p, person.getPlainPassword());
        personBean.generateAuthToken(p);
        return p;
    }


    /*public Response login( Person person) {
        Person p = personRepository.findOptionalByEmail(person.getEmail());
        if (p == null) {
            throw new EntityNotFoundException();
        }
         try {
        personBean.checkPassword(p, person.getPlainPassword());
        personBean.generateAuthToken(p);
          } catch (EJBTransactionRolledbackException e) {
              e.printStackTrace();
              return Response.status(401).build();
          }
        return Response.status(200).entity(p).build();
    }*/

    public Person get() {
        //return authorizationBean.getCurrentUser();
        return null;
    }
}
