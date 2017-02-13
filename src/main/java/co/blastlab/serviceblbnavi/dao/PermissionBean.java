package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.PermissionRepository;
import co.blastlab.serviceblbnavi.domain.Permission;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
public class PermissionBean {

    @Inject
    private EntityManager em;

    @Inject
    private PermissionRepository permissionRepository;

    public List<String> getPermissions(Long personId, Long complexId) {
        List<Permission> permissions = permissionRepository.findByPersonIdAndComplexId(personId, complexId);
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
