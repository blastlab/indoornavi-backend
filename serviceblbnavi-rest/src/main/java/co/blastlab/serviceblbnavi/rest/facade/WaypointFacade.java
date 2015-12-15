/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.WaypointBean;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Grzegorz Konupek
 */
@Path("/waypoint")
@Api("/waypoint")
public class WaypointFacade {
    
    @EJB
    private WaypointBean waypointBean;

    @GET
    @Path("/building/{id: \\d+}")
    @ApiOperation(value = "create waypoint visit", response = Waypoint.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "invalid waypoint visit\'s data")
    })
    public List<Waypoint> getWaypointsByBuildingId(@ApiParam(value = "id", required = true) @PathParam("id") Long buildingId) {
        if (buildingId != null) {
            List<Waypoint> waypoints = waypointBean.findByBuildingId(buildingId);
            return waypoints;
        }
        throw new EntityNotFoundException();
    }
}
