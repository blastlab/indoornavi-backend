package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.domain.Permission;
import java.util.ArrayList;
import java.util.List;
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

    public List<String> getPermissions(Long personId, Long complexId) {
        List<Permission> permissions = em.createNamedQuery(Permission.FIND_BY_PERSON_ID_AND_COMPLEX_ID, Permission.class).setParameter("personId", personId).setParameter("complexId", complexId).getResultList();
        List<String> permissionStrings = new ArrayList<>();
        permissions.stream().forEach(permission -> {
            permissionStrings.add(permission.getName());
        });
        return permissionStrings;
    }

    public void checkPermission(Long personId, Long complexId, String permission) {
        if (!getPermissions(personId, complexId).contains(permission)) {
            throw new PermissionException();
        }
    }

}
