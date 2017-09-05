package co.blastlab.serviceblbnavi.rest.facade.sink;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SinkBean implements SinkFacade {

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Override
	public List<SinkDto> findAll() {
		return sinkRepository.findAll().stream().map(SinkDto::new).collect(Collectors.toList());
	}

	@Override
	public SinkDto create(SinkDto sink) {
		Sink sinkEntity = new Sink();
		return createOrUpdate(sinkEntity, sink);
	}

	@Override
	public SinkDto update(Long id, SinkDto sink) {
		Sink sinkEntity = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(sinkEntity, sink);
	}

	@Override
	public Response delete(Long id) {
		Sink sink = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		sinkRepository.remove(sink);
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	private SinkDto createOrUpdate(Sink sinkEntity, SinkDto sink) {
		sinkEntity.setName(sink.getName());
		sinkEntity.setShortId(sink.getShortId());
		sinkEntity.setLongId(sink.getLongId());
		sinkEntity.setVerified(sink.getVerified());
		sinkEntity.setFloor(sink.getFloorId() != null ? floorRepository.findOptionalById(sink.getFloorId()).orElseThrow(EntityNotFoundException::new) : null);
		List<Anchor> anchors = new ArrayList<>();
		sink.getAnchors().forEach(anchorDto -> {
			anchors.add(anchorRepository.findById(anchorDto.getId()).orElseThrow(EntityNotFoundException::new));
		});
		sinkEntity.setAnchors(anchors);
		sinkEntity.setConfigured(sink.getConfigured());
		sinkRepository.save(sinkEntity);
		return new SinkDto(sinkEntity);
	}
}
