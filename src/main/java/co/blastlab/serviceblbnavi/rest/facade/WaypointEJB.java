package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.dto.waypoint.WaypointDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;


public class WaypointEJB implements WaypointFacade {

    @Inject
    private WaypointBean waypointBean;

    @Inject
    private FloorBean floorBean;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private BuildingBean buildingBean;

    @Inject
    BuildingRepository buildingRepository;


    public WaypointDto createWaypoint(WaypointDto waypoint) {
        if (waypoint.getFloorId() != null) {
            Floor floor = floorRepository.findBy(waypoint.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                Waypoint waypointEntity = new Waypoint();
                waypointEntity.setInactive(waypoint.isInactive());
                waypointEntity.setY(waypoint.getY());
                waypointEntity.setX(waypoint.getX());
                waypointEntity.setDistance(waypoint.getDistance());
                waypointEntity.setTimeToCheckout(waypoint.getTimeToCheckout());
                waypointEntity.setDetails(waypoint.getDetails());
                waypointEntity.setName(waypoint.getName());
                waypointEntity.setFloor(floor);
                waypointBean.create(waypointEntity);
                return new WaypointDto(waypointEntity);
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public WaypointDto updateWaypoint(WaypointDto waypoint) {
        if (waypoint.getId() != null) {
            Waypoint waypointEntity = waypointBean.findById(waypoint.getId());
            if (waypointEntity != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointEntity.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointEntity.setName(waypoint.getName());
                waypointEntity.setDetails(waypoint.getDetails());
                waypointEntity.setDistance(waypoint.getDistance());
                waypointEntity.setTimeToCheckout(waypoint.getTimeToCheckout());
                waypointEntity.setX(waypoint.getX());
                waypointEntity.setY(waypoint.getY());
                waypointBean.update(waypointEntity);
                return new WaypointDto(waypointEntity);
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public WaypointDto updateWaypointsCoordinates(WaypointDto waypoint) {
        if (waypoint.getId() != null) {
            Waypoint waypointEntity = waypointBean.findById(waypoint.getId());
            if (waypointEntity != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointEntity.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointEntity.setX(waypoint.getX());
                waypointEntity.setY(waypoint.getY());
                waypointBean.update(waypointEntity);
                return new WaypointDto(waypointEntity);
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }


    public List<WaypointDto> getActiveWaypointsByFloorId(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointBean.findActiveByFloorId(floorId);
                return convertToDtos(waypoints);
            }
        }
        throw new EntityNotFoundException();
    }


    public List<WaypointDto> getWaypointsByBuildingId(Long buildingId) {
        if (buildingId != null) {
            Building building = buildingRepository.findBy(buildingId);
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointBean.findByBuildingId(buildingId);
                return convertToDtos(waypoints);
            }
            throw new EntityNotFoundException();
        }
        throw new EntityNotFoundException();
    }
    

    public WaypointDto deactivate(Long waypointId) {
        Waypoint waypoint = waypointBean.findById(waypointId);
        if (waypoint != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    waypoint.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            waypointBean.deactivate(waypoint);
            return new WaypointDto(waypoint);
        }
        throw new EntityNotFoundException();
    }

    private List<WaypointDto> convertToDtos(List<Waypoint> waypoints) {
        List<WaypointDto> dtos = new ArrayList<>();
        waypoints.forEach(waypoint -> dtos.add(new WaypointDto(waypoint)));
        return dtos;
    }
}
