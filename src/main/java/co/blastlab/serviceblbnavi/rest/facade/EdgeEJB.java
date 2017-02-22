package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.EdgeBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.dto.edge.EdgeDto;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
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


    public List<EdgeDto> create(List<EdgeDto> edges) {
        List<Edge> edgeEntities = new ArrayList<>();
        edges.forEach((edge) -> {
            Edge edgeEntity = new Edge();
            Vertex source = vertexBean.find(edge.getSourceId());
            Vertex target = vertexBean.find(edge.getTargetId());
            if (source != null && target != null && edgeBean.findBySourceAndTarget(edge.getSourceId(), edge.getTargetId()) == null) {
                permissionBean.checkPermission(source, Permission.UPDATE);
                edgeEntity.setWeight(edge.getWeight());
                edgeEntity.setTarget(target);
                edgeEntity.setSource(source);
                edgeEntities.add(edgeEntity);
            } else {
                throw new EntityNotFoundException();
            }
        });
        edgeBean.create(edgeEntities);
        return convertToDtos(edgeEntities);
    }


    public List<EdgeDto> findByVertexFloorId(Long id) {
        if (id != null) {
            List<Edge> result = edgeBean.findByVertexFloorId(id);
            if (result.size() > 0) {
                permissionBean.checkPermission(result.get(0).getSource(), Permission.READ);
                return convertToDtos(result);
            }
        }
        throw new EntityNotFoundException();
    }


    public List<EdgeDto> findByVertexId(Long vertexId) {
        if (vertexId != null) {
            List<Edge> result = edgeBean.findByVertexId(vertexId);
            if (result.size() > 0) {
                permissionBean.checkPermission(result.get(0).getSource(), Permission.READ);
                return convertToDtos(result);
            }
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long sourceId, Long targetId) {
        Edge firstEdge = edgeBean.findBySourceAndTarget(sourceId, targetId);
        Edge secondEdge = edgeBean.findBySourceAndTarget(targetId, sourceId);
        if (firstEdge != null) {
            permissionBean.checkPermission(firstEdge.getSource(), Permission.UPDATE);
            edgeBean.delete(firstEdge);
        }
        if (secondEdge != null) {
            permissionBean.checkPermission(secondEdge.getSource(), Permission.UPDATE);
            edgeBean.delete(secondEdge);
        }
        return Response.ok().build();
    }


    public EdgeDto findBySourceIdAndTargetId(Long sourceId, Long targetId) {
        Edge edge = edgeBean.findBySourceAndTarget(sourceId, targetId);
        if (edge != null) {
            permissionBean.checkPermission(edge.getSource(), Permission.UPDATE);
            return new EdgeDto(edge);
        }
        throw new EntityNotFoundException();
    }


    public List<EdgeDto> update(List<EdgeDto> edges) {
        List<Edge> newEdges = new ArrayList<>();
        for (EdgeDto edge : edges) {
            Edge edgeEntity = edgeBean.find(edge.getId());
            if (edgeEntity.getSource() == null) {
                Vertex source = vertexBean.find(edge.getSourceId());
                if (source != null) {
                    edgeEntity.setSource(source);
                }
            }
            if (edgeEntity.getTarget() == null) {
                Vertex target = vertexBean.find(edge.getTargetId());
                if (target != null) {
                    edgeEntity.setTarget(target);
                }
            }
            if (edgeEntity.getSource() == null || edgeEntity.getTarget() == null) {
                throw new BadRequestException();
            }
            Edge newEdge = edgeBean.findBySourceAndTarget(edge.getSourceId(), edge.getTargetId());
            if (newEdge == null) {
                throw new BadRequestException();
            }
            permissionBean.checkPermission(newEdge.getSource(), Permission.UPDATE);
            newEdge.setWeight(edge.getWeight());
            newEdges.add(newEdge);
        }
        edgeBean.update(newEdges);
        return convertToDtos(newEdges);
    }


    private List<EdgeDto> convertToDtos(List<Edge> edgeEntities) {
        List<EdgeDto> edges = new ArrayList<>();
        edgeEntities.forEach((edgeEntity) -> edges.add(new EdgeDto(edgeEntity)));
        return edges;
    }
}
