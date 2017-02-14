package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.WaypointRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class WaypointEJB implements WaypointFacade {

    @Inject
    private WaypointRepository waypointRepository;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private BuildingRepository buildingRepository;


    public Waypoint createWaypoint(Waypoint waypoint) {
        if (waypoint.getFloorId() != null) {
            Floor floor = floorRepository.findBy(waypoint.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                waypoint.setFloor(floor);
                waypointRepository.save(waypoint);
                return waypoint;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public Waypoint updateWaypoint(Waypoint newWaypoint) {
        if (newWaypoint.getId() != null) {
            Waypoint waypointInDB = waypointRepository.findBy(newWaypoint.getId());
            if (waypointInDB != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointInDB.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointInDB.setName(newWaypoint.getName());
                waypointInDB.setDetails(newWaypoint.getDetails());
                waypointInDB.setDistance(newWaypoint.getDistance());
                waypointInDB.setTimeToCheckout(newWaypoint.getTimeToCheckout());
                waypointInDB.setX(newWaypoint.getX());
                waypointInDB.setY(newWaypoint.getY());
                waypointRepository.save(waypointInDB);
                return waypointInDB;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }
    

    public Waypoint updateWaypointsCoordinates(Waypoint newWaypoint) {
        if (newWaypoint.getId() != null) {
            Waypoint waypointInDB = waypointRepository.findBy(newWaypoint.getId());
            if (waypointInDB != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        waypointInDB.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                waypointInDB.setX(newWaypoint.getX());
                waypointInDB.setY(newWaypoint.getY());
                waypointRepository.save(waypointInDB);
                return waypointInDB;
            }
            throw new EntityNotFoundException();
        }
        throw new BadRequestException();
    }


    public List<Waypoint> getActiveWaypointsByFloorId(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                //List<Waypoint> waypoints = waypointBean.findActiveByFloorId(floorId);
                List<Waypoint> waypoints = waypointRepository.findByFloorAndInactive(floor, false);
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

                List<Floor> floors = floorRepository.findByBuilding(building);
                List<Waypoint> waypoints = new ArrayList<>();
                floors.stream().forEach((floor) -> {
                    waypoints.addAll(waypointRepository.findByFloor(floor));
                });
                //List<Waypoint> waypoints = waypointBean.findByBuildingId(buildingId);
                return waypoints;
            }
            throw new EntityNotFoundException();
        }
        throw new EntityNotFoundException();
    }
    

    public Waypoint deactivate(Long waypointId) {
        //Waypoint waypoint = waypointBean.findById(waypointId);
        Waypoint waypoint = waypointRepository.findBy(waypointId);
        if (waypoint != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    waypoint.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            waypoint.setInactive(true);
            waypointRepository.save(waypoint);
            return waypoint;
        }
        throw new EntityNotFoundException();
    }
}
