package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/edge")
@Api("/edge")
@TokenAuthorization
public interface EdgeFacade {

    @POST
    @ApiOperation(value = "create edges", response = Edge.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "target or source id emtpy or doesn't exist")
    })
    List<Edge> create(@ApiParam(value = "edges", required = true) List<Edge> edges);



    @PUT
    @ApiOperation(value = "update edges", response = Edge.class, responseContainer = "List")
    List<Edge> update(@ApiParam(value = "edges", required = true) List<Edge> edges);


    @DELETE
    @ApiOperation(value = "delete edge", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
    })
    Response delete(
            @ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
            @ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId);


    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find edges by floor id", response = Edge.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<Edge> findByVertexFloorId(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/vertex/{id: \\d+}")
    @ApiOperation(value = "find edges by vertex id", response = Edge.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    List<Edge> findByVertexId(@PathParam("id") @ApiParam(value = "id", required = true) Long vertexId);

    @GET
    @ApiOperation(value = "find edge", response = Edge.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
    })
    Edge findBySourceIdAndTargetId(
            @ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
            @ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId);
}
