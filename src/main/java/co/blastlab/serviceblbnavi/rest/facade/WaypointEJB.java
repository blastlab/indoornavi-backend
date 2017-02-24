package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.WaypointRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.dto.waypoint.WaypointDto;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class WaypointEJB extends UpdaterBean<WaypointDto, Waypoint> implements WaypointFacade {

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
    private WaypointRepository waypointRepository;


    public WaypointDto createWaypoint(WaypointDto waypoint) {
        Floor floor = floorRepository.findBy(waypoint.getFloorId());
        if (floor != null) {
            permissionBean.checkPermission(floor, Permission.UPDATE);
            Waypoint waypointEntity = new Waypoint();
            waypointEntity.setInactive(waypoint.isInactive());
            waypointEntity.setY(waypoint.getY());
            waypointEntity.setX(waypoint.getX());
            waypointEntity.setDistance(waypoint.getDistance());
            waypointEntity.setTimeToCheckout(waypoint.getTimeToCheckout());
            waypointEntity.setDetails(waypoint.getDetails());
            waypointEntity.setName(waypoint.getName());
            waypointEntity.setFloor(floor);
            waypointEntity = waypointRepository.save(waypointEntity);
            return new WaypointDto(waypointEntity);
        }
        throw new BadRequestException();
    }
    

    public WaypointDto updateWaypoint(WaypointDto waypoint) {
        if (waypoint.getId() != null) {
            Waypoint waypointEntity = waypointRepository.findBy(waypoint.getId());
            if (waypointEntity != null) {
                permissionBean.checkPermission(waypointEntity, Permission.UPDATE);
                waypointEntity.setName(waypoint.getName());
                waypointEntity.setDetails(waypoint.getDetails());
                waypointEntity.setDistance(waypoint.getDistance());
                waypointEntity.setTimeToCheckout(waypoint.getTimeToCheckout());
                waypointEntity.setX(waypoint.getX());
                waypointEntity.setY(waypoint.getY());
                waypointEntity = waypointRepository.save(waypointEntity);
                return new WaypointDto(waypointEntity);
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public WaypointDto updateWaypointsCoordinates(WaypointDto waypoint) {
        return super.updateCoordinates(waypoint, waypointRepository);
    }


    public List<WaypointDto> getActiveWaypointsByFloorId(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(floor, Permission.READ);
                List<Waypoint> waypoints = waypointRepository.findByFloor(floor);
                return convertToDtos(waypoints);
            }
        }
        throw new EntityNotFoundException();
    }


    public List<WaypointDto> getWaypointsByBuildingId(Long buildingId) {
        if (buildingId != null) {
            Building building = buildingRepository.findBy(buildingId);
            if (building != null) {
                permissionBean.checkPermission(building, Permission.READ);
                List<Waypoint> waypoints = new ArrayList<>();
                building.getFloors().forEach(floor -> waypoints.addAll(waypointRepository.findByFloor(floor)));
                return convertToDtos(waypoints);
            }
            throw new EntityNotFoundException();
        }
        throw new EntityNotFoundException();
    }
    

    public WaypointDto deactivate(Long waypointId) {
        WaypointDto waypoint = new WaypointDto();
        waypoint.setId(waypointId);
        return super.deactivate(waypoint, waypointRepository);
    }


    private List<WaypointDto> convertToDtos(List<Waypoint> waypoints) {
        List<WaypointDto> dtos = new ArrayList<>();
        waypoints.forEach(waypoint -> dtos.add(new WaypointDto(waypoint)));
        return dtos;
    }
}
