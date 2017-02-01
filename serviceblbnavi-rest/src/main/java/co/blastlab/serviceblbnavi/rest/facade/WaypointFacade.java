package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import java.util.List;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/waypoint")
@Api("/waypoint")
public class WaypointFacade {

    @EJB
    private WaypointBean waypointBean;

    @EJB
    private FloorBean floorBean;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @EJB
    private BuildingBean buildingBean;

    @Inject
    BuildingRepository buildingRepository;

    @POST
    @ApiOperation(value = "create waypoint", response = Waypoint.class)
    @ApiResponses({
        @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    public Waypoint createWaypoint(@ApiParam(value = "waypoint", required = true) Waypoint waypoint) {
        if (waypoint.getFloorId() != null) {
            Floor floor = floorBean.find(waypoint.getFloorId());
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
    
    @PUT
    @ApiOperation(value = "update waypoint's data", response = Waypoint.class)
    @ApiResponses({
        @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    public Waypoint updateWaypoint(@ApiParam(value = "waypoint", required = true) Waypoint waypoint) {
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
    
    @PUT
    @Path("/coordinates")
    @ApiOperation(value = "update waypoint's coordinates", response = Waypoint.class)
    @ApiResponses({
        @ApiResponse(code = 400, message = "invalid waypoint's data")
    })
    public Waypoint updateWaypointsCoordinates(@ApiParam(value = "waypoint", required = true) Waypoint waypoint) {
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

    @GET
    @Path("/floor/{id: \\d+}/active")
    @ApiOperation(value = "gets active waypoints by floor id", response = Waypoint.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building id was not found")
    })
    public List<Waypoint> getActiveWaypointsByFloorId(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId) {
        if (floorId != null) {
            Floor floor = floorBean.find(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                List<Waypoint> waypoints = waypointBean.findActiveByFloorId(floorId);
                return waypoints;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/building/{id: \\d+}")
    @ApiOperation(value = "gets waypoints by building id", response = Waypoint.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "building id was not found")
    })
    public List<Waypoint> getWaypointsByBuildingId(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId) {
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
    
    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.WaypointInternal.class)
    @ApiOperation(value = "deactivates waypoint of given id", response = Waypoint.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "waypoint with given id wasn't found")
    })
    public Waypoint deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long waypointId) {
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
