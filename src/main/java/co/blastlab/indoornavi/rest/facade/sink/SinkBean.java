package co.blastlab.indoornavi.rest.facade.sink;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.sink.SinkDto;
import co.blastlab.indoornavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SinkBean implements SinkFacade {

	@Inject
	private Logger logger;

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
		logger.debug("Trying to create sink {}", sink);
		Sink sinkEntity = new Sink();
		return createOrUpdate(sinkEntity, sink);
	}

	@Override
	public SinkDto update(Long id, SinkDto sink) {
		logger.debug("Trying to update sink {}", sink);
		Sink sinkEntity = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(sinkEntity, sink);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove sink id {}", id);
		Sink sink = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		sinkRepository.remove(sink);
		logger.debug("Sink removed");
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	private SinkDto createOrUpdate(Sink sinkEntity, SinkDto sink) {
		sinkEntity.setName(sink.getName());
		sinkEntity.setShortId(sink.getShortId());
		sinkEntity.setMac(sink.getMacAddress());
		sinkEntity.setVerified(sink.getVerified());
		sinkEntity.setFloor(sink.getFloor() != null ? floorRepository.findOptionalById(sink.getFloor().getId()).orElseThrow(EntityNotFoundException::new) : null);
		sinkEntity.getAnchors().clear();
		sink.getAnchors().forEach(anchorDto -> {
			sinkEntity.getAnchors().add(anchorRepository.findById(anchorDto.getId()).orElseThrow(EntityNotFoundException::new));
		});
		sinkEntity.setConfigured(sink.getConfigured() != null ? sink.getConfigured() : false);
		sinkRepository.save(sinkEntity);
		logger.debug("Sink created/updated");
		return new SinkDto(sinkEntity);
	}
}
