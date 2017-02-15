package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.VertexBean;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.dto.vertex.VertexDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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


    public VertexDto create(VertexDto vertex) {
        if (vertex.getFloorId() != null) {
            Floor floor = floorRepository.findBy(vertex.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                Vertex vertexEntity = new Vertex();
                vertexEntity.setX(vertex.getX());
                vertexEntity.setY(vertex.getY());
                vertexEntity.setFloor(floor);
                vertexEntity.setInactive(vertex.isInactive());
                vertexRepository.save(vertexEntity);
                return new VertexDto(vertexEntity, vertex.isFloorUpChangeable(), vertex.isFloorDownChangeable());
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


    public VertexDto update(VertexDto vertex) {
        System.out.println(vertex.getId() + " " + vertex.getX() + " " + vertex.getY());
        if (vertex.getId() != null) {
            Vertex vertexEntity = vertexBean.find(vertex.getId());
            //Vertex v = vertexRepository.findOptionalBy(vertex.getId());
            if (vertexEntity != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertexEntity.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
                vertexEntity.setX(vertex.getX());
                vertexEntity.setY(vertex.getY());
                vertexBean.update(vertexEntity);
                return new VertexDto(vertexEntity);
            }
        }
        throw new EntityNotFoundException();
    }


    public List<VertexDto> findByFloor(Long floorId) {
        if (floorId != null) {
            List<Vertex> vertices = vertexBean.findAll(floorId);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return convertToDtos(vertices);
        }
        throw new EntityNotFoundException();
    }


    public List<VertexDto> findAllActiveByFloor(Long floorId) {
        if (floorId != null) {
            List<Vertex> vertices = vertexBean.findAllActive(floorId);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        vertices.get(0).getFloor().getBuilding().getComplex().getId(), Permission.READ);
            }
            return convertToDtos(vertices);
        }
        throw new EntityNotFoundException();
    }


    public VertexDto findById(Long vertexId) {
        if (vertexId != null) {
            Vertex vertex = vertexBean.find(vertexId);
            if (vertex != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), 
                        vertex.getFloor().getBuilding().getComplex().getId(), Permission.READ);
                vertex.setFloor(vertex.getFloor());
                return new VertexDto(vertex);
            }
        }
        throw new EntityNotFoundException();
    }


    public VertexDto deactivate(Long vertexId) {
        Vertex vertex = vertexBean.find(vertexId);
        if (vertex != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), 
                    vertex.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            vertexBean.deactivate(vertex);
            return new VertexDto(vertex);
        }
        throw new EntityNotFoundException();
    }

    private List<VertexDto> convertToDtos(List<Vertex> vertices) {
        List<VertexDto> vertexDtos = new ArrayList<>();
        vertices.forEach((vertex -> vertexDtos.add(new VertexDto(vertex))));
        return vertexDtos;
    }

}
