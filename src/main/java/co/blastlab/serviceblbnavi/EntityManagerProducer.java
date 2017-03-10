package co.blastlab.serviceblbnavi;

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
}
