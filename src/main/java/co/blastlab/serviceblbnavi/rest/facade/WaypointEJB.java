package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import java.util.List;


public class WaypointEJB implements WaypointFacade {

    @Inject
    private WaypointBean waypointBean;

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


    public Waypoint createWaypoint(Waypoint waypoint) {
        if (waypoint.getFloorId() != null) {
            //Floor floor = floorBean.find(waypoint.getFloorId());
            Floor floor = floorRepository.findBy(waypoint.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                waypoint.setFloor(floor);
                waypointBean.create(waypoint);
                return waypoint;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public Waypoint updateWaypoint(Waypoint waypoint) {
        if (waypoint.getId() != null) {
            Waypoint waypointInDB = waypointBean.findById(waypoint.getId());
            if (waypointInDB != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointInDB.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointInDB.setName(waypoint.getName());
                waypointInDB.setDetails(waypoint.getDetails());
                waypointInDB.setDistance(waypoint.getDistance());
                waypointInDB.setTimeToCheckout(waypoint.getTimeToCheckout());
                waypointInDB.setX(waypoint.getX());
                waypointInDB.setY(waypoint.getY());
                waypointBean.update(waypointInDB);
                return waypointInDB;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public Waypoint updateWaypointsCoordinates(Waypoint waypoint) {
        if (waypoint.getId() != null) {
            Waypoint waypointInDB = waypointBean.findById(waypoint.getId());
            if (waypointInDB != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointInDB.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointInDB.setX(waypoint.getX());
                waypointInDB.setY(waypoint.getY());
                waypointBean.update(waypointInDB);
                return waypointInDB;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }


    public List<Waypoint> getActiveWaypointsByFloorId(Long floorId) {
        if (floorId != null) {
            //Floor floor = floorBean.find(floorId);
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointBean.findActiveByFloorId(floorId);
                return waypoints;
            }
        }
        throw new EntityNotFoundException();
    }


    public List<Waypoint> getWaypointsByBuildingId(Long buildingId) {
        if (buildingId != null) {
            //Building building = buildingBean.find(buildingId);
            Building building = buildingRepository.findBy(buildingId);
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointBean.findByBuildingId(buildingId);
                return waypoints;
            }
            throw new EntityNotFoundException();
        }
        throw new EntityNotFoundException();
    }
    

    public Waypoint deactivate(Long waypointId) {
        Waypoint waypoint = waypointBean.findById(waypointId);
        if (waypoint != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    waypoint.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            waypointBean.deactivate(waypoint);
            return waypoint;
        }
        throw new EntityNotFoundException();
    }
}
