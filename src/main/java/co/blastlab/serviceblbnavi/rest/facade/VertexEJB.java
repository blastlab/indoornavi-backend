package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.dto.vertex.VertexDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.rest.bean.UpdaterBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Stateless
public class VertexEJB extends UpdaterBean<VertexDto, Vertex> implements VertexFacade {

    @Inject
    private VertexRepository vertexRepository;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public VertexDto create(VertexDto vertex) {
        Floor floor = floorRepository.findBy(vertex.getFloorId());
        if (floor != null) {
            permissionBean.checkPermission(floor, Permission.UPDATE);
            Vertex vertexEntity = new Vertex();
            vertexEntity.setX(vertex.getX());
            vertexEntity.setY(vertex.getY());
            vertexEntity.setFloor(floor);
            vertexEntity.setInactive(vertex.isInactive());
            vertexEntity = vertexRepository.save(vertexEntity);
            return new VertexDto(vertexEntity, vertex.isFloorUpChangeable(), vertex.isFloorDownChangeable());
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Vertex vertex = vertexRepository.findBy(id);
        if (vertex != null) {
            permissionBean.checkPermission(vertex, Permission.UPDATE);
            vertexRepository.removeAndFlush(vertex);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();

    }


    public VertexDto update(VertexDto vertex) {
        return super.updateCoordinates(vertex, vertexRepository);
    }


    public List<VertexDto> findByFloor(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            List<Vertex> vertices = vertexRepository.findByFloor(floor);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(vertices.get(0), Permission.READ);
            }
            return convertToDtos(vertices);
        }
        throw new EntityNotFoundException();
    }


    public List<VertexDto> findAllActiveByFloor(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            List<Vertex> vertices = vertexRepository.findByFloorAndInactive(floor, false);
            if (vertices.size() > 0) {
                permissionBean.checkPermission(vertices.get(0), Permission.READ);
            }
            return convertToDtos(vertices);
        }
        throw new EntityNotFoundException();
    }


    public VertexDto findById(Long vertexId) {
        if (vertexId != null) {
            Vertex vertex = vertexRepository.findBy(vertexId);
            if (vertex != null) {
                permissionBean.checkPermission(vertex, Permission.READ);
                vertex.setFloor(vertex.getFloor());
                return new VertexDto(vertex);
            }
        }
        throw new EntityNotFoundException();
    }


    public VertexDto deactivate(Long vertexId) {
        VertexDto vertex = new VertexDto();
        vertex.setId(vertexId);
        return super.deactivate(vertex, vertexRepository);
    }


    private List<VertexDto> convertToDtos(List<Vertex> vertices) {
        List<VertexDto> vertexDtos = new ArrayList<>();
        vertices.forEach((vertex -> vertexDtos.add(new VertexDto(vertex))));
        return vertexDtos;
    }

}
