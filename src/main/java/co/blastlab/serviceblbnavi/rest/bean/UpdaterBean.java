package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import org.apache.deltaspike.data.api.EntityRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;

@Stateless
public abstract class UpdaterBean<T extends Updatable> {

    @Inject
    private FloorRepository floorRepository;  //<T, Long>

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    PermissionBean permissionBean;

    public T create(T entity, EntityRepository repository) {
        if (entity.getFloorId() != null) {
            Floor floor = floorRepository.findBy(entity.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                entity.setFloor(floor);
                repository.save(entity);
                return entity;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }

    public T updateCoordinates(T entity, EntityRepository<T, Long> repository) {
        if (entity.getId() != null) {
            T entityInDB = repository.findBy(entity.getId());
            if (entityInDB != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        entityInDB.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                entityInDB.setX(entity.getX());
                entityInDB.setY(entity.getY());
                repository.save(entityInDB);
                return entityInDB;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }

    public T deactivate(Long id, EntityRepository<T, Long> repository) {
        T entity = repository.findBy(id);
        if (entity != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    entity.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            entity.setInactive(true);
            repository.save(entity);
            return entity;
        }
        throw new EntityNotFoundException();
    }
}
