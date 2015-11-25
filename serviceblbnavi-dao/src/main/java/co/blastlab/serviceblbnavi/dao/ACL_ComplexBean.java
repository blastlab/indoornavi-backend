package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.ACL_Complex;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class ACL_ComplexBean {
    
    @Inject
    private EntityManager em;
    
    public void create(ACL_Complex aclComplex) {
        em.persist(aclComplex);
    }
    
    public void create(List<ACL_Complex> aclComplexes) {
        aclComplexes.stream().forEach((aclComplex) -> {
            em.persist(aclComplex);
        });
    }
    
}
