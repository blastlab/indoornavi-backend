package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.EdgeRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.VertexRepository;
import co.blastlab.serviceblbnavi.domain.Edge;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Vertex;
import co.blastlab.serviceblbnavi.dto.edge.EdgeDto;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EdgeBean implements EdgeFacade {

	@Inject
	private EdgeRepository edgeRepository;

	@Inject
	private VertexRepository vertexRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private PermissionBean permissionBean;

	public List<EdgeDto> create(List<EdgeDto> edges) {
		List<Edge> edgeEntities = new ArrayList<>();
		edges.forEach((edge) -> {
			Edge edgeEntity = new Edge();
			Vertex source = vertexRepository.findBy(edge.getSourceId());
			Vertex target = vertexRepository.findBy(edge.getTargetId());
			if (source != null && target != null && edgeRepository.findOptionalBySourceAndTarget(source, target) == null) {
				permissionBean.checkPermission(source, Permission.UPDATE);
				edgeEntity.setWeight(edge.getWeight());
				edgeEntity.setTarget(target);
				edgeEntity.setSource(source);
				edgeEntities.add(edgeEntity);
			} else {
				throw new EntityNotFoundException();
			}
		});
		save(edgeEntities);
		return convertToDtos(edgeEntities);
	}

	private void save(List<Edge> edges) {
		edges.stream().forEach((edge) -> {
			edgeRepository.save(edge);
		});
	}

	public List<EdgeDto> findByVertexFloorId(Long floorId) {
		if (floorId != null) {
			Floor floor = floorRepository.findBy(floorId);
			List<Vertex> vertices = vertexRepository.findByFloor(floor);
			List<Edge> result = new ArrayList<>();
			vertices.stream().forEach((vertex) -> {
				result.addAll(edgeRepository.findBySource(vertex));
			});
			if (result.size() > 0) {
				permissionBean.checkPermission(result.get(0).getSource(), Permission.READ);
			}
			return convertToDtos(result);
		}
		throw new EntityNotFoundException();
	}

	public List<EdgeDto> findByVertexId(Long vertexId) {
		if (vertexId != null) {
			Vertex vertex = vertexRepository.findBy(vertexId);
			List<Edge> result = edgeRepository.findBySource(vertex);
			if (result.size() > 0) {
				permissionBean.checkPermission(result.get(0).getSource(), Permission.READ);
			}
			return convertToDtos(result);
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long sourceId, Long targetId) {
		Vertex source = vertexRepository.findBy(sourceId);
		Vertex target = vertexRepository.findBy(targetId);

		Edge firstEdge = edgeRepository.findOptionalBySourceAndTarget(source, target);
		Edge secondEdge = edgeRepository.findOptionalBySourceAndTarget(target, source);

		if (firstEdge != null) {
			permissionBean.checkPermission(firstEdge.getSource(), Permission.UPDATE);
			edgeRepository.remove(firstEdge);
		}
		if (secondEdge != null) {
			permissionBean.checkPermission(secondEdge.getSource(), Permission.UPDATE);
			edgeRepository.remove(secondEdge);
		}
		return Response.ok().build();
	}

	public EdgeDto findBySourceIdAndTargetId(Long sourceId, Long targetId) {
		Vertex source = vertexRepository.findBy(sourceId);
		Vertex target = vertexRepository.findBy(targetId);
		Edge edge = edgeRepository.findOptionalBySourceAndTarget(source, target);

		if (edge != null) {
			permissionBean.checkPermission(edge.getSource(), Permission.UPDATE);
			return new EdgeDto(edge);
		}
		throw new EntityNotFoundException();
	}

	public List<EdgeDto> update(List<EdgeDto> edges) {
		List<Edge> newEdges = new ArrayList<>();
		for (EdgeDto edge : edges) {
			Edge edgeEntity = edgeRepository.findBy(edge.getId());
			if (edgeEntity.getSource() == null) {
				Vertex source = vertexRepository.findBy(edge.getSourceId());
				if (source != null) {
					edgeEntity.setSource(source);
				}
			}
			if (edgeEntity.getTarget() == null) {
				Vertex target = vertexRepository.findBy(edge.getTargetId());
				if (target != null) {
					edgeEntity.setTarget(target);
				}
			}
			if (edgeEntity.getSource() == null || edgeEntity.getTarget() == null) {
				throw new BadRequestException();
			}
			Vertex source = vertexRepository.findBy(edge.getSourceId());
			Vertex target = vertexRepository.findBy(edge.getTargetId());
			Edge newEdge = edgeRepository.findOptionalBySourceAndTarget(source, target);
			if (newEdge == null) {
				throw new BadRequestException();
			}
			permissionBean.checkPermission(newEdge.getSource(), Permission.UPDATE);
			newEdge.setWeight(edge.getWeight());
			newEdges.add(newEdge);
		}
		save(newEdges);
		return convertToDtos(newEdges);
	}

	private List<EdgeDto> convertToDtos(List<Edge> edgeEntities) {
		List<EdgeDto> edges = new ArrayList<>();
		edgeEntities.forEach((edgeEntity) -> edges.add(new EdgeDto(edgeEntity)));
		return edges;
	}
}
