package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class BuildingBean {

	@Inject
	private EntityManager em;

	public void create(Building building) {
		em.persist(building);
	}

	public Building find(Long id) {
		return em.find(Building.class, id);
	}

	public List<Building> findByComplex(Complex complex) {
		return em.createNamedQuery(Building.FIND_BY_COMPLEX, Building.class).setParameter("complex", complex).getResultList();
	}

	public void delete(Building building) {
		em.remove(em.contains(building) ? building : em.merge(building));
	}

	public void update(Building building) {
		em.merge(building);
	}

}
