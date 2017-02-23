package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/vertex")
@Api("/vertex")
@TokenAuthorization
public interface VertexFacade {


    @POST
    @ApiOperation(value = "create vertex", response = Vertex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id emtpy or floor doesn't exist")
    })
    @JsonView(View.VertexInternal.class)
    Vertex create(@ApiParam(value = "vertex", required = true) Vertex vertex);

    @PUT
    @ApiOperation(value = "update vertex", response = Vertex.class)
    @JsonView(View.VertexInternal.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex id empty or doesn't exist")
    })
    Vertex update(@ApiParam(value = "vertex", required = true) Vertex vertex);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete vertex", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id doesn't exist")
    })
    Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find vertexes for specified floor", response = Vertex.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<Vertex> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);

    @GET
    @Path("/floor/{id: \\d+}/active")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find active vertices for specified floor", response = Vertex.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    List<Vertex> findAllActiveByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId);

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find vertex by id", response = Vertex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    Vertex findById(@ApiParam(value = "id", required = true) @PathParam("id") Long vertexId);

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "deactivates vertex of given id", response = Vertex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    Vertex deactivate(@ApiParam(value = "id", required = true) @PathParam("id") Long vertexId);
}