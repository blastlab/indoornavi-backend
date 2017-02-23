package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;

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


    public FloorDto create(FloorDto floor) {
        Building building = buildingRepository.findBy(floor.getBuildingId());
        if (building != null) {
            permissionBean.checkPermission(building, Permission.UPDATE);
            Floor floorEntity = new Floor();
            floorEntity.setLevel(floor.getLevel());
            floorEntity.setBuilding(building);
            floorEntity = floorRepository.save(floorEntity);
            return new FloorDto(floorEntity);
        }
        throw new EntityNotFoundException();
    }


    public FloorDto find(Long id) {
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(floor, Permission.READ);
            return new FloorDto(floor);
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(floor, Permission.UPDATE);
            floorRepository.remove(floor);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public FloorDto update(FloorDto floor) {
        Building building = buildingRepository.findBy(floor.getBuildingId());
        if (building != null) {
            permissionBean.checkPermission(building, Permission.UPDATE);
            Floor floorEntity = floorRepository.findBy(floor.getId());
            floorEntity.setLevel(floor.getLevel());
            floorEntity.setBuilding(building);
            floor.setBuildingId(building.getId());
            floorEntity = floorRepository.save(floorEntity);
            return new FloorDto(floorEntity);
        }
        throw new EntityNotFoundException();
    }


    public Response updateFloors(Long buildingId, List<FloorDto> floors) {
        Building building = buildingRepository.findBy(buildingId);
        if (building != null) {
            permissionBean.checkPermission(building, Permission.UPDATE);
            List<Floor> floorEntities = new ArrayList<>();
            floors.forEach((floor -> floorEntities.add(floorRepository.findBy(floor.getId()))));
            floorEntities.forEach((floorEntity -> floorEntity.setBuilding(building)));
            floorBean.updateFloorLevels(floorEntities);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public Response updatemToPix(FloorDto.Extended floor) {
        Floor floorEntity = floorRepository.findBy(floor.getId());
        if (floorEntity != null) {
            permissionBean.checkPermission(floorEntity, Permission.UPDATE);
            floorEntity.setMToPix(floor.getMToPix());
            floorRepository.save(floorEntity);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

}