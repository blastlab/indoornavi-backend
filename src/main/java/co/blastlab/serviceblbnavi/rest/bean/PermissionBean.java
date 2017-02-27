package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.PermissionRepository;
import co.blastlab.serviceblbnavi.domain.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PermissionBean {

	@Inject
	private PermissionRepository permissionRepository;

	@Inject
	private AuthorizationBean authorizationBean;

	public List<String> getPermissions(Long personId, Long complexId) {
		List<Permission> permissions = permissionRepository.findByPersonIdAndComplexId(personId, complexId);
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
