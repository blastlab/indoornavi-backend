package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.EdgeBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Vertex;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author mkoszalka
 */
@Path("/edge")
@Api("/edge")
public class EdgeFacade {

    @EJB
    private EdgeBean edgeBean;

    @EJB
    private VertexBean vertexBean;

    @EJB
    private FloorBean floorBean;

    @POST
    @ApiOperation(value = "create edge", response = Edge.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "target or source id emtpy or doesn't exist")
    })
    public Edge create(@ApiParam(value = "edge", required = true) Edge edge) {
        if (edge.getSourceId() != null && edge.getTargetId() != null) {
            Vertex source = vertexBean.find(edge.getSourceId());
            Vertex target = vertexBean.find(edge.getTargetId());
            if (source != null && target != null && edgeBean.findBySourceAndTarget(source, target) == null) {
                edge.setTarget(target);
                edge.setSource(source);
                edgeBean.create(edge);
                return edge;
            }
        }
        throw new EntityNotFoundException();
    }

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find edges by floor id", response = Edge.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "floor with given id wasn't found")
    })
    public List<Edge> findByVertexFloorId(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
        if (id != null) {
            Long time = System.currentTimeMillis();
            List<Edge> result = edgeBean.findByVertexFloorId(id);
            System.out.println("query duration: " + (System.currentTimeMillis() - time));
            return result;
        }
        throw new EntityNotFoundException();
    }

    @DELETE
    @ApiOperation(value = "delete edge", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "edge with given target and source id doesn't exist")
    })
    public Response delete(@ApiParam(value = "sourceId", required = true) @HeaderParam("sourceId") Long sourceId, @ApiParam(value = "targetId", required = true) @HeaderParam("targetId") Long targetId) {
        System.out.println("inside deleting method");
        Vertex source = vertexBean.find(sourceId);
        Vertex target = vertexBean.find(targetId);
        Edge firstEdge = edgeBean.findBySourceAndTarget(source, target);
        Edge secondEdge = edgeBean.findBySourceAndTarget(target, source);
        if (firstEdge != null) {
            edgeBean.delete(firstEdge);
        }
        if (secondEdge != null) {
            edgeBean.delete(secondEdge);
        }
        return Response.ok().build();
    }

    @PUT
    @ApiOperation(value = "update edge", response = Edge.class)
    public Edge update(@ApiParam(value = "edge", required = true) Edge edge) {
        edgeBean.update(edge);
        return edge;
    }

    @PUT
    @ApiOperation(value = "update edges", response = Edge.class, responseContainer = "List")
    public List<Edge> update(@ApiParam(value = "edges", required = true) List<Edge> edges) {
        edgeBean.update(edges);
        return edges;
    }
}
