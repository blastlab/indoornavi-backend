package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.EdgeBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import com.wordnik.swagger.annotations.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */
@Path("/edge")
@Api("/edge")
public class EdgeFacade {

    @Inject
    private EdgeBean edgeBean;

    @Inject
    private VertexBean vertexBean;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create edges", response = Edge.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "target or source id emtpy or doesn't exist")
    })
    public List<Edge> create(@ApiParam(value = "edges", required = true) List<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.getSourceId() != null && edge.getTargetId() != null && edge.getWeight() != null) {
                Vertex source = vertexBean.find(edge.getSourceId());
                Vertex target = vertexBean.find(edge.getTargetId());
                if (source != null && target != null && edgeBean.findBySourceAndTarget(edge.getSourceId(), edge.getTargetId()) == null) {
                    permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                            source.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                    edge.setTarget(target);
                    edge.setSource(source);
                    continue;
                }
            }
            throw new EntityNotFoundException();
        }
        edgeBean.create(edges);
        return edges;
    }

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find edges by floor id", response = Edge.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Edge> findByVertexFloorId(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        if (id != null) {
            List<Edge> result = edgeBean.findByVertexFloorId(id);
            if (result.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        result.get(0).getSource().getFloor().getBuilding().getComplex().getId(),
                        Permission.READ);
            }
            return result;
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/vertex/{id: \\d+}")
    @ApiOperation(value = "find edges by vertex id", response = Edge.class, responseContainer = "List")
    @ApiResponses({
        @ApiResponse(code = 404, message = "vertex with given id wasn't found")
    })
    public List<Edge> findByVertexId(@PathParam("id") @ApiParam(value = "id", required = true) Long vertexId) {
        if (vertexId != null) {
            List<Edge> result = edgeBean.findByVertexId(vertexId);
            if (result.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        result.get(0).getSource().getFloor().getBuilding().getComplex().getId(),
                        Permission.READ);
            }
            return result;
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @ApiOperation(value = "delete edge", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
    })
    public Response delete(
            @ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
            @ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId) {
        Edge firstEdge = edgeBean.findBySourceAndTarget(sourceId, targetId);
        Edge secondEdge = edgeBean.findBySourceAndTarget(targetId, sourceId);
        if (firstEdge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    firstEdge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            edgeBean.delete(firstEdge);
        }
        if (secondEdge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    secondEdge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            edgeBean.delete(secondEdge);
        }
        return Response.ok().build();
    }

    @GET
    @ApiOperation(value = "find edge", response = Edge.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
    })
    public Edge findBySourceIdAndTargetId(
            @ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId,
            @ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId) {
        Edge edge = edgeBean.findBySourceAndTarget(sourceId, targetId);
        if (edge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    edge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            return edge;
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @ApiOperation(value = "update edges", response = Edge.class, responseContainer = "List")
    public List<Edge> update(@ApiParam(value = "edges", required = true) List<Edge> edges) {
        List<Edge> newEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource() == null && edge.getSourceId() != null) {
                Vertex source = vertexBean.find(edge.getSourceId());
                if (source != null) {
                    edge.setSource(source);
                }
            }
            if (edge.getTarget() == null && edge.getTargetId() != null) {
                Vertex target = vertexBean.find(edge.getTargetId());
                if (target != null) {
                    edge.setTarget(target);
                }
            }
            if (edge.getSource() == null || edge.getTarget() == null) {
                throw new BadRequestException();
            }
            Edge newEdge = edgeBean.findBySourceAndTarget(edge.getSourceId(), edge.getTargetId());
            if (newEdge == null) {
                throw new BadRequestException();
            }
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    newEdge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            newEdge.setWeight(edge.getWeight());
            newEdges.add(newEdge);
        }
        edgeBean.update(newEdges);
        return newEdges;
    }
}
