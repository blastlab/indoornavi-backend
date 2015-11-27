package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BeaconBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Michał Koszałka
 */
@Path("/beacon")
@Api("/beacon")
public class BeaconFacade {

    @EJB
    private BeaconBean beaconBean;

    @EJB
    private FloorBean floorBean;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create beacon", response = Beacon.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    public Beacon create(@ApiParam(value = "beacon", required = true) Beacon beacon) {
        if (beacon.getFloorId() != null) {
            Floor floor = floorBean.find(beacon.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                beacon.setFloor(floor);
                beaconBean.create(beacon);
                return beacon;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find beacon", response = Beacon.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "beacon with given id wasn't found")
    })
    public Beacon find(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Beacon beacon = beaconBean.find(id);
        if (beacon != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    beacon.getFloor().getBuilding().getComplex().getId(), Permission.READ);
            return beacon;
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete beacon", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "beacon with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Beacon beacon = beaconBean.find(id);
        if (beacon != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    beacon.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            beaconBean.delete(beacon);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @ApiOperation(value = "update beacon", response = Beacon.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor id or floor empty or doesn't exist")
    })
    public Beacon update(@ApiParam(value = "beacon", required = true) Beacon beacon) {
        if (beacon.getFloorId() != null) {
            Floor floor = floorBean.find(beacon.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                beacon.setFloor(floor);
                beaconBean.update(beacon);
                return beacon;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/floor/{id: \\d+}")
    @ApiOperation(value = "find beacons by floor id", response = Beacon.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floorId empty or floor doesn't exist")
    })
    public List<Beacon> findAll(@PathParam("id") @ApiParam(value = "id", required = true) Long floorId) {
        if (floorId != null) {
            Floor floor = floorBean.find(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                return beaconBean.findAll(floor);
            }
        }
        throw new EntityNotFoundException();
    }
}
