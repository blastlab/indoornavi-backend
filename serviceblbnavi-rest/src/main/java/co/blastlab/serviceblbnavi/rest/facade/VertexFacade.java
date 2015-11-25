package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
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
@Path("/vertex")
@Api("/vertex")
public class VertexFacade {

    @EJB
    private VertexBean vertexBean;

    @EJB
    private FloorBean floorBean;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create vertex", response = Vertex.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor id emtpy or floor doesn't exist")
    })
    @JsonView(View.VertexInternal.class)
    public Vertex create(@ApiParam(value = "vertex", required = true) Vertex vertex) {
        if (vertex.getFloorId() != null) {
            Floor floor = floorBean.find(vertex.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                vertex.setFloor(floor);
                vertexBean.create(vertex);
                return vertex;
            }
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete vertex", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id doesn't exist")
    })
    public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        Vertex vertex = vertexBean.find(id);
        if (vertex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    vertex.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            vertexBean.delete(vertex);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();

    }

    @PUT
    @ApiOperation(value = "update vertex", response = Vertex.class)
    @JsonView(View.VertexInternal.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex id empty or doesn't exist")
    })
    public Vertex update(@ApiParam(value = "vertex", required = true) Vertex vertex) {
        System.out.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
        if (vertex.getId() != null) {
            Vertex v = vertexBean.find(vertex.getId());
            if (v != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        v.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                v.setX(vertex.getX());
                v.setY(vertex.getY());
                vertexBean.update(v);
                return v;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find vertexes for specified floor", response = Vertex.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Vertex> findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId) {
        if (floorId != null) {
            List<Vertex> vertices = vertexBean.findAll(floorId);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return vertices;
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/floor/{id: \\d+}/active")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find active vertices for specified floor", response = Vertex.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Vertex> findAllActiveByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long floorId) {
        if (floorId != null) {
            List<Vertex> vertices = vertexBean.findAllActive(floorId);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return vertices;
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.VertexInternal.class)
    @ApiOperation(value = "find vertex by id", response = Vertex.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    public Vertex findById(@ApiParam(value = "id", required = true) @PathParam("id") Long vertexId) {
        if (vertexId != null) {
            Vertex vertex = vertexBean.find(vertexId);
            if (vertex != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), 
                        vertex.getFloor().getBuilding().getComplex().getId(), Permission.READ);
                vertex.setFloorId(vertex.getFloor().getId());
                return vertex;
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @Path("/{id: \\d+}/deactivate")
    @JsonView(View.GoalInternal.class)
    @ApiOperation(value = "deactivates vertex of given id", response = Vertex.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    public Vertex dectivate(@ApiParam(value = "id", required = true) @PathParam("id") Long vertexId) {
        Vertex vertex = vertexBean.find(vertexId);
        if (vertex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), 
                    vertex.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            vertexBean.deactivate(vertex);
            return vertex;
        }
        throw new EntityNotFoundException();
    }

}
