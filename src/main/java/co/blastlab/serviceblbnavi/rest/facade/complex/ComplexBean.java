package co.blastlab.serviceblbnavi.rest.facade.complex;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.service.FloorService;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class ComplexBean implements ComplexFacade {

	private final static Logger LOGGER = LoggerFactory.getLogger(ComplexBean.class);

	private final ComplexRepository complexRepository;

	private final SinkRepository sinkRepository;

	private final FloorService floorService;

	@Inject
	public ComplexBean(ComplexRepository complexRepository, SinkRepository sinkRepository, FloorService floorService) {
		this.complexRepository = complexRepository;
		this.sinkRepository = sinkRepository;
		this.floorService = floorService;
	}

	@Override
	public ComplexDto create(ComplexDto complex) {
		LOGGER.debug("Trying to create complex {}", complex);
		Complex complexEntity = new Complex();
		complexEntity.setName(complex.getName());
		complexEntity = complexRepository.save(complexEntity);
		LOGGER.debug("Complex created");
		return new ComplexDto(complexEntity);
	}


	@Override
	public ComplexDto update(Long id, ComplexDto complex) {
		LOGGER.debug("Trying to update complex {}", complex);
		Optional<Complex> complexEntity = complexRepository.findById(id);
		if (complexEntity.isPresent()){
			complexEntity.get().setName(complex.getName());
			Complex complexDb = complexRepository.save(complexEntity.get());
			LOGGER.debug("Complex updated");
			return new ComplexDto(complexDb);
		}
		throw new EntityNotFoundException();
	}


	@Override
	public Response delete(Long id) {
		LOGGER.debug("Trying to remove complex id = {}", id);
		Complex complex = complexRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		for (Building building : complex.getBuildings()) {
			for (Floor floor : building.getFloors()) {
				floorService.remove(floor);
			}
		}
		complexRepository.remove(complex);
		LOGGER.debug("Complex removed");
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}


	@Override
	public List<ComplexDto> findAll() {
		List<ComplexDto> complexes = new ArrayList<>();
		complexRepository.findAll()
			.forEach(complexEntity -> complexes.add(new ComplexDto(complexEntity)));
		return complexes;
	}


	@Override
	public ComplexDto.WithBuildings findWithBuildings(Long id) {
		Optional<Complex> complexEntity = complexRepository.findById(id);
		if (complexEntity.isPresent()) {
			return new ComplexDto.WithBuildings(complexEntity.get());
		}
		throw new EntityNotFoundException();
	}
}