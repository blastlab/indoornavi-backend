package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.security.PasswordEncoder;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * @author Michał Koszałka
 */
@Stateless
public class PersonBean{

    @Inject
    private EntityManager em;

    @Inject
    private PersonBean personBean;

    @Inject
    private PersonRepository personRepository;


    public void create(Person person) {
        em.persist(person);
    }

    public Person find(Long id) {
        return em.find(Person.class, id);
    }

    public void update(Person person) {
        em.merge(person);
    }


    public Person findByEmail(String email) {
        try {
            return em.createNamedQuery(Person.FIND_BY_EMAIL, Person.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("NoResultException in findByEmail");
        }
        return null;
    }

    public Person findByAuthToken(String authToken) {
        try {
            return em.createNamedQuery(Person.FIND_BY_AUTH_TOKEN, Person.class).setParameter("authToken", authToken).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("NoResultException in findByAuthToken");
        }
        return null;
    }

    public void checkPassword(Person person, String plainPassword) {
        if (!PasswordEncoder.getShaPassword(Person.PASSWORD_DIGEST_ALG, plainPassword, person.getSalt()).equalsIgnoreCase(person.getPassword())) {
            throw new PermissionException();
        }
    }

    public String generateAuthToken(Person person) {
        String token = PasswordEncoder.getAuthToken();
        person.setAuthToken(token);
        update(person);
        return token;
    }

}
