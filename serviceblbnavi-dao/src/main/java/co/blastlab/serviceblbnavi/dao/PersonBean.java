package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Person;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class PersonBean {

    @Inject
    private EntityManager em;

    public void create(Person person) {
        Complex complex = new Complex();
        complex.setName("complex");
        em.persist(person);
        em.refresh(person);
        complex.setPerson(person);
        em.persist(complex);
    }

    public Person find(Long id) {
        return em.find(Person.class, id);
    }

    public Person findByEmail(String email) {
        Person person;
        try {
            person = em.createNamedQuery(Person.FIND_BY_EMAIL, Person.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            person = null;
        }
        return person;
    }

    public Person findByAuthToken(String authToken) {
        Person person;
        try {
            person = em.createNamedQuery(Person.FIND_BY_AUTH_TOKEN, Person.class).setParameter("authToken", authToken).getSingleResult();
        } catch (NoResultException e) {
            person = null;
        }
        return person;
    }

}
