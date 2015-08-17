/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BeaconBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ejb.EJB;
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
 * @author root
 */
@Path("/beacon")
@Api("/beacon")
public class BeaconFacade {

    @EJB
    private BeaconBean beaconBean;

    @EJB
    private FloorBean floorBean;

    @POST
    @ApiOperation(value = "create beacon", response = Beacon.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    public Beacon create(@ApiParam(value = "beacon", required = true) Beacon beacon) {
        if (beacon.getFloorId() != null) {
            Floor floor = floorBean.find(beacon.getFloorId());
            if (floor != null) {
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
        if (beacon == null) {
            throw new EntityNotFoundException();
        }
        return beacon;
    }

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete beacon", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "beacon with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Beacon beacon = beaconBean.find(id);
        if (beacon == null) {
            throw new EntityNotFoundException();
        }
        beaconBean.delete(beacon);
        return Response.ok().build();
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
                beacon.setFloor(floor);
                beaconBean.update(beacon);
                return beacon;
            }
        }
        throw new EntityNotFoundException();
    }

}
