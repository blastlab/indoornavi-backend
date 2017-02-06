package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */
@Path("/floor")
@Api("/floor")
@Stateless
public class FloorFacade {

    @Inject
    private FloorBean floorBean;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private BuildingRepository buildingRepository;

    @Inject
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
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                floor.setBuilding(building);
                floor.setBuildingId(building.getId());
                //floorBean.create(floor);
                floorRepository.save(floor);
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
        //Floor floor = floorBean.find(id);
        Floor floor = floorRepository.findBy(id);
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
        //Floor floor = floorBean.find(id);
        Floor floor = floorRepository.findBy(id);
        if (floor != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.UPDATE);
            //floorBean.delete(floor);
            floorRepository.remove(floor);
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
            //Building building = buildingBean.find(floor.getBuildingId());
            Building building = buildingRepository.findBy(floor.getBuildingId());
            if (building != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        building.getComplex().getId(), Permission.UPDATE);
                floor.setBuilding(building);
                floor.setBuildingId(building.getId());
                //floorBean.update(floor);
                floorRepository.save(floor);
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
        //Building building = buildingBean.find(buildingId);
        Building building = buildingRepository.findBy(buildingId);
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
        Floor floorInDB = find(floor.getId());
        if (floorInDB != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floorInDB.getBuilding().getComplex().getId(), Permission.UPDATE);
            floorInDB.setmToPix(floor.getmToPix());
            //floorBean.update(floorInDB);
            floorRepository.save(floorInDB);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }

}
