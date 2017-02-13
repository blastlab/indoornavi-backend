package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.security.PasswordEncoder;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Michał Koszałka
 */
@Stateless
public class PersonBean{

    @Inject
    private PersonRepository personRepository;

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
