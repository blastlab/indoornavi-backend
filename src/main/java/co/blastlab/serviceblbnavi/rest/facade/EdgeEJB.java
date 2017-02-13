package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.EdgeBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


public class EdgeEJB implements EdgeFacade {

    @Inject
    private EdgeBean edgeBean;

    @Inject
    private VertexBean vertexBean;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public List<Edge> create(List<Edge> edges) {
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


    public List<Edge> findByVertexFloorId(Long id) {
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


    public List<Edge> findByVertexId(Long vertexId) {
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


    public Response delete(Long sourceId, Long targetId) {
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


    public Edge findBySourceIdAndTargetId(Long sourceId, Long targetId) {
        Edge edge = edgeBean.findBySourceAndTarget(sourceId, targetId);
        if (edge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    edge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            return edge;
        }
        throw new EntityNotFoundException();
    }


    public List<Edge> update(List<Edge> edges) {
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
