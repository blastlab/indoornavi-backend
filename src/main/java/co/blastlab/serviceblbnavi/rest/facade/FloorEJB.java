package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
public class FloorEJB implements FloorFacade {

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public Floor create(Floor floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                return createOrUpdate(floor, building);
            }
        }
        throw new EntityNotFoundException();
    }


    public Floor find(Long id) {
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.READ);
            floor.setBuildingId(floor.getBuilding().getId());
            return floor;
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


    public Floor update(Floor floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                return createOrUpdate(floor, building);
            }
        }
        throw new EntityNotFoundException();
    }

    private Floor createOrUpdate(Floor floor, Building building){
        permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                building.getComplex().getId(), Permission.UPDATE);
        floor.setBuilding(building);
        floor.setBuildingId(building.getId());
        floorRepository.save(floor);
        return floor;
    }

    public Response updateFloors(Long buildingId, List<Floor> floors) {
        Building building = buildingRepository.findBy(buildingId);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            for (Floor floor : floors) {
                floor.setBuilding(building);
                updateFloorLevels(floors);
                return Response.ok().build();
            }
        }
        throw new EntityNotFoundException();
    }


    public Response updatemToPix(Floor floor) {
        Floor floorInDB = find(floor.getId());
        if (floorInDB != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floorInDB.getBuilding().getComplex().getId(), Permission.UPDATE);
            floorInDB.setMToPix(floor.getMToPix());
            floorRepository.save(floorInDB);
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
}