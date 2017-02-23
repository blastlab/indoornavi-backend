package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
public class VertexEJB extends UpdaterBean<Vertex> implements VertexFacade{

    @Inject
    private VertexRepository vertexRepository;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public Vertex create(Vertex vertex) {
        return this.create(vertex, vertexRepository);
    }


    public Response delete(Long id) {
        Vertex vertex = vertexRepository.findBy(id);
        if (vertex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    vertex.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            vertexRepository.remove(vertex);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();

    }


    public Vertex update(Vertex vertex) {
        return this.updateCoordinates(vertex, vertexRepository);
    }


    public List<Vertex> findByFloor(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            List<Vertex> vertices = vertexRepository.findByFloor(floor);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return vertices;
        }
        throw new EntityNotFoundException();
    }


    public List<Vertex> findAllActiveByFloor(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            List<Vertex> vertices = vertexRepository.findByFloorAndInactive(floor, false);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return vertices;
        }
        throw new EntityNotFoundException();
    }


    public Vertex findById(Long vertexId) {
        if (vertexId != null) {
            Vertex vertex = vertexRepository.findBy(vertexId);
            if (vertex != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertex.getFloor().getBuilding().getComplex().getId(), Permission.READ);
                vertex.setFloorId(vertex.getFloor().getId());
                return vertex;
            }
        }
        throw new EntityNotFoundException();
    }


    public Vertex deactivate(Long vertexId) {
        return this.deactivate(vertexId, vertexRepository);
    }

}
