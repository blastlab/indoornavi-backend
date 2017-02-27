package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import co.blastlab.serviceblbnavi.rest.facade.ext.UpdatableEntity;
import org.apache.deltaspike.data.api.EntityRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;

@Stateless
public abstract class UpdaterBean<T extends Updatable, S extends UpdatableEntity> {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private AuthorizationBean authorizationBean;

	@Inject
	private PermissionBean permissionBean;

	protected T updateCoordinates(T dto, EntityRepository<S, Long> repository) {
		if (dto.getId() != null) {
			S entity = repository.findBy(dto.getId());
			if (entity != null) {
				permissionBean.checkPermission(dto.getFloorId(), Permission.UPDATE);
				entity.setX(dto.getX());
				entity.setY(dto.getY());
				entity = repository.save(entity);
				return (T) dto.create(entity);
			}
			throw new EntityNotFoundException();
		}
		throw new BadRequestException();
	}

	protected T deactivate(T dto, EntityRepository<S, Long> repository) {
		S entity = repository.findBy(dto.getId());
		if (entity != null) {
			permissionBean.checkPermission(entity.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
			entity.setInactive(true);
			repository.save(entity);
			return (T) dto.create(entity);
		}
		throw new EntityNotFoundException();
	}
}
