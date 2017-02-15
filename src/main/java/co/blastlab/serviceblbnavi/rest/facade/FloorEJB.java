package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Stateless
public class FloorEJB implements FloorFacade {

    @Inject
    private FloorBean floorBean;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public FloorDto create(FloorDto floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                // TODO: why Permnission.UPDATE when there is Permission.CREATE?
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                Floor floorEntity = new Floor();
                floorEntity.setMToPix(floor.getMToPix());
                floorEntity.setStartZoom(floor.getStartZoom());
                floorEntity.setBitmapWidth(floor.getBitmapWidth());
                floorEntity.setBitmapHeight(floor.getBitmapHeight());
                floorEntity.setLevel(floor.getLevel());
                floorEntity.setBuilding(building);
                floorEntity = floorRepository.save(floorEntity);
                return new FloorDto(floorEntity);
            }
        }
        throw new EntityNotFoundException();
    }


    public FloorDto find(Long id) {
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.READ);
            return new FloorDto(floor);
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.UPDATE);
            floorRepository.remove(floor);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public FloorDto update(FloorDto floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                Floor floorEntity = floorRepository.findBy(floor.getId());
                floorEntity.setMToPix(floor.getMToPix());
                floorEntity.setStartZoom(floor.getStartZoom());
                floorEntity.setBitmapWidth(floor.getBitmapWidth());
                floorEntity.setBitmapHeight(floor.getBitmapHeight());
                floorEntity.setLevel(floor.getLevel());
                floorEntity.setBuilding(building);
                floor.setBuildingId(building.getId());
                floorEntity = floorRepository.save(floorEntity);
                return new FloorDto(floorEntity);
            }
        }
        throw new EntityNotFoundException();
    }


    public Response updateFloors(Long buildingId, List<FloorDto> floors) {
        Building building = buildingRepository.findBy(buildingId);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            List<Floor> floorEntities = new ArrayList<>();
            floors.forEach((floor -> floorEntities.add(floorRepository.findBy(floor.getId()))));
            floorEntities.forEach((floorEntity -> floorEntity.setBuilding(building)));
            floorBean.updateFloorLevels(floorEntities);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public Response updatemToPix(FloorDto floor) {
        Floor floorEntity = floorRepository.findBy(floor.getId());
        if (floorEntity != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floorEntity.getBuilding().getComplex().getId(), Permission.UPDATE);
            floorEntity.setMToPix(floor.getMToPix());
            floorRepository.save(floorEntity);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

}