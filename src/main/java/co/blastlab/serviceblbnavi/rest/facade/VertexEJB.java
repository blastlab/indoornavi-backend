package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
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
public class VertexEJB implements VertexFacade {

    @Inject
    private VertexBean vertexBean;

    @Inject
    private VertexRepository vertexRepository;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public Vertex create(Vertex vertex) {
        if (vertex.getFloorId() != null) {
            Floor floor = floorRepository.findBy(vertex.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                vertex.setFloor(floor);
                //vertexBean.create(vertex);
                vertexRepository.save(vertex);
                return vertex;
            }
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Vertex vertex = vertexBean.find(id);
        //Vertex vertex = vertexRepository.findOptionalBy(id);
        if (vertex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    vertex.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            //vertexBean.delete(vertex);
            vertexRepository.removeAndFlush(vertex);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();

    }


    public Vertex update(Vertex vertex) {
        System.out.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
        if (vertex.getId() != null) {
            Vertex v = vertexBean.find(vertex.getId());
            //Vertex v = vertexRepository.findOptionalBy(vertex.getId());
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


    public List<Vertex> findByFloor(Long floorId) {
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


    public List<Vertex> findAllActiveByFloor(Long floorId) {
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


    public Vertex findById(Long vertexId) {
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


    public Vertex dectivate(Long vertexId) {
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
