package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Person;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class ComplexBean {

	@Inject
	private EntityManager em;

	public void create(Complex complex) {
		em.persist(complex);
	}

	public Complex find(Long id) {
		return em.find(Complex.class, id);
	}

	public List<Complex> findAll(Person person) {
		return em.createNamedQuery(Complex.FIND_BY_PERSON, Complex.class).getResultList();
	}

}
