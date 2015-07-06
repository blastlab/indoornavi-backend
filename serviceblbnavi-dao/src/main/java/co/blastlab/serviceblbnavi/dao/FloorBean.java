package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Floor;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class FloorBean {

	@Inject
	private EntityManager em;

	public void add(Floor floor) {
		em.persist(floor);
	}

}
