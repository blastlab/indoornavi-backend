package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PermissionBean {

    @Inject
    private EntityManager em;

    @Inject
    private AuthorizationBean authorizationBean;

    public Permission findByName(String name) {
        return em.createNamedQuery(Permission.FIND_BY_NAME, Permission.class).setParameter("name", name).getSingleResult();
    }

    public List<String> getPermissions(Long personId, Long complexId) {
        List<Permission> permissions = em.createNamedQuery(Permission.FIND_BY_PERSON_ID_AND_COMPLEX_ID, Permission.class).setParameter("personId", personId).setParameter("complexId", complexId).getResultList();
        List<String> permissionStrings = new ArrayList<>();
        permissions.forEach(permission -> {
            permissionStrings.add(permission.getName());
        });
        return permissionStrings;
    }

    public void checkPermission(Long personId, Long complexId, String permission) {
        if (!getPermissions(personId, complexId).contains(permission)) {
            throw new PermissionException();
        }
    }

    public void checkPermission(Long complexId, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), complexId, permission);
    }

    public void checkPermission(Complex complex, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), complex.getId(), permission);
    }

    public void checkPermission(Building building, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), building.getComplex().getId(), permission);
    }

    public void checkPermission(Floor floor, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), floor.getBuilding().getComplex().getId(), permission);
    }

    public void checkPermission(Beacon beacon, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), beacon.getFloor().getBuilding().getComplex().getId(), permission);
    }

    public void checkPermission(Vertex vertex, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), vertex.getFloor().getBuilding().getComplex().getId(), permission);
    }

    public void checkPermission(Goal goal, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), goal.getFloor().getBuilding().getComplex().getId(), permission);
    }

    public void checkPermission(Waypoint waypoint, String permission) {
        checkPermission(authorizationBean.getCurrentUser().getId(), waypoint.getFloor().getBuilding().getComplex().getId(), permission);
    }
}
