package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
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
@Path("/floor")
@Api("/floor")
public class FloorFacade {

    @EJB
    private FloorBean floorBean;

    @EJB
    private BuildingBean buildingBean;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create floor", response = Floor.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    public Floor create(@ApiParam(value = "floor", required = true) Floor floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingBean.find(floor.getBuildingId());
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                floor.setBuilding(building);
                floor.setBuildingId(building.getId());
                floorBean.create(floor);
                return floor;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.FloorInternal.class)
    @ApiOperation(value = "find floor", response = Floor.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public Floor find(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Floor floor = floorBean.find(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.READ);
            floor.setBuildingId(floor.getBuilding().getId());
            return floor;
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete floor", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Floor floor = floorBean.find(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.UPDATE);
            floorBean.delete(floor);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @JsonView(View.FloorInternal.class)
    @ApiOperation(value = "update floor", response = Floor.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    public Floor update(@ApiParam(value = "floor", required = true) Floor floor) {
        if (floor.getBuildingId() != null) {
            Building building = buildingBean.find(floor.getBuildingId());
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                floor.setBuilding(building);
                floor.setBuildingId(building.getId());
                floorBean.update(floor);
                return floor;
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/{id: \\d+}")
    @ApiOperation(value = "update floors", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
    })
    public Response updateFloors(@PathParam("id") Long buildingId, @ApiParam(value = "floors", required = true) List<Floor> floors) {
        Building building = buildingBean.find(buildingId);
        if (building != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    building.getComplex().getId(), Permission.UPDATE);
            for (Floor floor : floors) {
                floor.setBuilding(building);
                floorBean.updateFloorLevels(floors);
                return Response.ok().build();
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/mToPix")
    @ApiOperation(value = "update mToPix", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id doesn't exist")
    })
    public Response updatemToPix(@ApiParam(value = "floor", required = true) Floor floor) {
        Floor f = find(floor.getId());
        if (f != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    f.getBuilding().getComplex().getId(), Permission.UPDATE);
            f.setmToPix(floor.getmToPix());
            floorBean.update(f);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

}
