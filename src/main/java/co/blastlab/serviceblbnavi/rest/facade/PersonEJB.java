package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;


@Stateless
public class PersonEJB implements PersonFacade {

    @Inject
    private PersonBean personBean;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private AuthorizationBean authorizationBean;


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


    public Person login(Person person) {
        Person p = personRepository.findOptionalByEmail(person.getEmail());

        if (p == null) {
            throw new EntityNotFoundException();
        }

        personBean.checkPassword(p, person.getPlainPassword());
        personBean.generateAuthToken(p);
        return p;
    }


    public Person get() {
        return authorizationBean.getCurrentUser();
    }

}
