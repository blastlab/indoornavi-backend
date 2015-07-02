package co.blastlab.serviceblbnavi.dao;

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
	
	public void create(Person person){
		em.persist(person);
		em.refresh(person);
	}
	
	public Person get(Long id) {
		return em.find(Person.class, id);
	}
	
	public Person getByEmail(String email) {
		Person person;
		try {
			person = em.createNamedQuery(Person.FIND_BY_EMAIL, Person.class).setParameter("email", email).getSingleResult();
		} catch(NoResultException e) {
			person = null;
		}
		return person;
	}
	
}
