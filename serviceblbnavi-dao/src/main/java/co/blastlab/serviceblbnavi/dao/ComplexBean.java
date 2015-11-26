package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Complex;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        em.flush();
    }

    public Complex find(Long id) {
        return em.find(Complex.class, id);
    }

    public void delete(Complex complex) {
        em.remove(em.contains(complex) ? complex : em.merge(complex));

    }

    public List<Complex> findAllByPerson(Long personId) {
        List<Complex> complexes = em.createNamedQuery(Complex.FIND_BY_PERSON, Complex.class).setParameter("personId", personId).getResultList();
        Set<Complex> complexSet = new HashSet<>(complexes);
        complexes = new ArrayList<>(complexSet);

        complexes.stream().forEach((complex) -> {
            List<String> permissions = new ArrayList<>();
            complex.getACL_complexes().stream().forEach((aclComplex) -> {
                permissions.add(aclComplex.getPermission().getName());
            });
            complex.setPermissions(permissions);
        });

        return complexes;
    }

    public Complex findByPersonAndId(Long personId, Long id) {
        return em.createNamedQuery(Complex.FIND_BY_PERSON_AND_ID, Complex.class).setParameter("personId", personId).setParameter("id", id).getSingleResult();
    }

    public Complex findByBuildingId(Long id) {
        return em.createNamedQuery(Complex.FIND_BY_BUILDING, Complex.class).setParameter("buildingId", id).getSingleResult();
    }

    public Complex findByFloorId(Long id) {
        return em.createNamedQuery(Complex.FIND_BY_FLOOR, Complex.class).setParameter("floorId", id).getSingleResult();
    }

}
