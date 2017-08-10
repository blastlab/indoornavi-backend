package co.blastlab.serviceblbnavi.rest.facade.sink;

import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
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
	private SinkRepository sinkRepository;

	@Override
	public List<SinkDto> findAll() {
		return sinkRepository.findAll().stream().map(SinkDto::new).collect(Collectors.toList());
	}

	@Override
	public SinkDto create(SinkDto sink) {
		Sink sinkEntity = new Sink();
		sinkEntity.setName(sink.getName());
		sinkEntity.setShortId(sink.getShortId());
		sinkEntity.setLongId(sink.getLongId());
		sinkEntity = sinkRepository.save(sinkEntity);
		return new SinkDto(sinkEntity);
	}

	@Override
	public SinkDto update(Long id, SinkDto sink) {
		Sink sinkEntity = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		sinkEntity.setName(sink.getName());
		sinkEntity.setShortId(sink.getShortId());
		sinkEntity.setLongId(sink.getLongId());
		sinkEntity = sinkRepository.save(sinkEntity);
		return new SinkDto(sinkEntity);
	}

	@Override
	public Response delete(Long id) {
		Sink sink = sinkRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		sinkRepository.remove(sink);
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}
}
