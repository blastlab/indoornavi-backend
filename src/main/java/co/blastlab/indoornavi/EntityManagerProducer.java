package co.blastlab.indoornavi;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EntityManagerProducer {

	@PersistenceContext(unitName = "NaviPU")
	private EntityManager entityManager;

	@Produces
	public EntityManager produceNaviEM() {
		return entityManager;
	}

	@Produces
	public SessionFactory produceSessionFactory() { return entityManager.unwrap(Session.class).getSessionFactory(); }
}
