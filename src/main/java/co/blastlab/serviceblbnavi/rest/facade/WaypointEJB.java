package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
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
public class WaypointEJB extends UpdaterBean<Waypoint> implements WaypointFacade {

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
        return this.create(waypoint, waypointRepository);
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


    public Waypoint updateWaypointsCoordinates(Waypoint waypoint) {
        return this.updateCoordinates(waypoint, waypointRepository);
    }


    public List<Waypoint> getActiveWaypointsByFloorId(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointRepository.findByFloorAndInactive(floor, false);
                return waypoints;
            }
        }
        throw new EntityNotFoundException();
    }


    public List<Waypoint> getWaypointsByBuildingId(Long buildingId) {
        if (buildingId != null) {
            Building building = buildingRepository.findBy(buildingId);
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.READ);

                List<Floor> floors = floorRepository.findByBuilding(building);
                List<Waypoint> waypoints = new ArrayList<>();
                floors.stream().forEach((floor) -> {
                    waypoints.addAll(waypointRepository.findByFloor(floor));
                });
                return waypoints;
            }
            throw new EntityNotFoundException();
        }
        throw new EntityNotFoundException();
    }


    public Waypoint deactivate(Long waypointId) {
        return this.deactivate(waypointId, waypointRepository);
    }
}
