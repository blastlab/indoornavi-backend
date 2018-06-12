package co.blastlab.serviceblbnavi.rest.facade.complex;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.service.FloorService;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class ComplexBean implements ComplexFacade {

	@Inject
	private Logger logger;

	private final ComplexRepository complexRepository;

	private final FloorService floorService;

	@Inject
	public ComplexBean(ComplexRepository complexRepository, FloorService floorService) {
		this.complexRepository = complexRepository;
		this.floorService = floorService;
	}

	@Override
	public ComplexDto create(ComplexDto complex) {
		logger.debug("Trying to create complex {}", complex);
		Complex complexEntity = new Complex();
		complexEntity.setName(complex.getName());
		complexEntity = complexRepository.save(complexEntity);
		logger.debug("Complex created");
		return new ComplexDto(complexEntity);
	}


	@Override
	public ComplexDto update(Long id, ComplexDto complex) {
		logger.debug("Trying to update complex {}", complex);
		Optional<Complex> complexEntity = complexRepository.findById(id);
		if (complexEntity.isPresent()){
			complexEntity.get().setName(complex.getName());
			Complex complexDb = complexRepository.save(complexEntity.get());
			logger.debug("Complex updated");
			return new ComplexDto(complexDb);
		}
		throw new EntityNotFoundException();
	}


	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove complex id = {}", id);
		Complex complex = complexRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		for (Building building : complex.getBuildings()) {
			for (Floor floor : building.getFloors()) {
				floorService.removeNoCommit(floor);
			}
		}
		complexRepository.remove(complex);
		logger.debug("Complex removed");
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