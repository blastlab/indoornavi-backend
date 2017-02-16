package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.security.PasswordEncoder;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;


@Stateless
public class PersonEJB implements PersonFacade {

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

        checkPassword(p, person.getPlainPassword());
        generateAuthToken(p);
        return p;
    }


    public Person get() {
        return authorizationBean.getCurrentUser();
    }

    public void checkPassword(Person person, String plainPassword) {
        if (!PasswordEncoder.getShaPassword(Person.PASSWORD_DIGEST_ALG, plainPassword, person.getSalt()).equalsIgnoreCase(person.getPassword())) {
            throw new PermissionException();
        }
    }

    public String generateAuthToken(Person person) {
        String token = PasswordEncoder.getAuthToken();
        person.setAuthToken(token);
        personRepository.save(person);
        return token;
    }

}
