package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.EdgeBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.EdgeRepository;
import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

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
    private EdgeRepository edgeRepository;

    @Inject
    private VertexRepository vertexRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public List<Edge> create(List<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.getSourceId() != null && edge.getTargetId() != null && edge.getWeight() != null) {
                Vertex source = vertexRepository.findBy(edge.getSourceId());
                Vertex target = vertexRepository.findBy(edge.getTargetId());
                if (source != null && target != null && edgeRepository.findOptionalBySourceAndTarget(source, target) == null) {
                    permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                            source.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                    edge.setTarget(target);
                    edge.setSource(source);
                    continue;
                }
            }
            throw new EntityNotFoundException();
        }
        //edgeBean.create(edges);
        this.save(edges);
        return edges;
    }

    private void save(List<Edge> edges) {
        edges.stream().forEach((edge) -> {
            edgeRepository.save(edge);
        });
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
        Vertex source = vertexRepository.findBy(sourceId);
        Vertex target = vertexRepository.findBy(targetId);

        Edge firstEdge = edgeRepository.findOptionalBySourceAndTarget(source, target);
        Edge secondEdge = edgeRepository.findOptionalBySourceAndTarget(target, source);
        /*Edge firstEdge = edgeBean.findOptionalBySourceAndTarget(sourceId, targetId);
        Edge secondEdge = edgeBean.findOptionalBySourceAndTarget(targetId, sourceId);*/
        if (firstEdge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    firstEdge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            //edgeBean.delete(firstEdge);
            edgeRepository.remove(firstEdge);
        }
        if (secondEdge != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    secondEdge.getSource().getFloor().getBuilding().getComplex().getId(),
                    Permission.UPDATE);
            //edgeBean.delete(secondEdge);
            edgeRepository.remove(secondEdge);
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
                Vertex source = vertexRepository.findBy(edge.getSourceId());
                if (source != null) {
                    edge.setSource(source);
                }
            }
            if (edge.getTarget() == null && edge.getTargetId() != null) {
                Vertex target = vertexRepository.findBy(edge.getTargetId());
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
        //edgeBean.update(newEdges);
        this.save(newEdges);
        return newEdges;
    }
}
