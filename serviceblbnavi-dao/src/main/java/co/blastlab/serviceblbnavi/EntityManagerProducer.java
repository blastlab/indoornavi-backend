package co.blastlab.serviceblbnavi;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class EntityManagerProducer {

	@PersistenceContext(unitName = "NaviPU")
	private EntityManager naviEM;

	@Produces
	public EntityManager produceNaviEM() {
		return naviEM;
	}

}
