package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Permission;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class PermissionBean {
    
    @Inject
    private EntityManager em;
    
    public Permission findByName(String name) {
        return em.createNamedQuery(Permission.FIND_BY_NAME, Permission.class).setParameter("name", name).getSingleResult();
    }
    
}
