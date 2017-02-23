package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
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
    private FloorRepository floorRepository;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private PermissionBean permissionBean;


    public FloorDto create(FloorDto floor) {
        Building building = buildingRepository.findBy(floor.getBuildingId());
        if (building != null) {
            Floor floorEntity = new Floor();
            return createOrUpdate(floorEntity, floor, building);
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
            Floor floorEntity = floorRepository.findBy(floor.getId());
            return createOrUpdate(floorEntity, floor, building);
        }
        throw new EntityNotFoundException();
    }

    public Response updateFloors(Long buildingId, List<FloorDto> floors) {
        Building building = buildingRepository.findBy(buildingId);
        if (building != null) {
            permissionBean.checkPermission(
                    building, Permission.UPDATE);
            List<Floor > floorEntities = new ArrayList<>() ;
                floors.forEach((floor-> floorEntities.add(floorRepository.findBy(floor.getId()))));
            floorEntities.forEach((floorEntity -> floorEntity.setBuilding(building)));
                updateFloorLevels(floorEntities);
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

    private void updateFloorLevels(List<Floor> floors) {
        floors.stream().map((f) -> {
            Floor floor = floorRepository.findBy(f.getId());
            floor.setLevel(f.getLevel());
            return floor;
        }).forEach((floor) -> {
            floorRepository.save(floor);
        });
    }

    private FloorDto createOrUpdate(Floor floorEntity, FloorDto floor, Building building){
        permissionBean.checkPermission(building, Permission.UPDATE);
        floorEntity.setLevel(floor.getLevel());
        floorEntity.setBuilding(building);
        floorEntity = floorRepository.save(floorEntity);
        return new FloorDto(floorEntity);
    }
}