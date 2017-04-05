package co.blastlab.serviceblbnavi.rest.facade.complex;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
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

	private final ComplexRepository complexRepository;

	@Inject
	public ComplexBean(ComplexRepository complexRepository) {
		this.complexRepository = complexRepository;
	}

	@Override
	public ComplexDto create(ComplexDto complex) {
		Complex complexEntity = new Complex();
		complexEntity.setName(complex.getName());
		complexEntity = complexRepository.save(complexEntity);
		return new ComplexDto(complexEntity);
	}


	@Override
	public ComplexDto update(Long id, ComplexDto complex) {
		Optional<Complex> complexEntity = complexRepository.findById(id);
		if (complexEntity.isPresent()){
			complexEntity.get().setName(complex.getName());
			Complex complexDb = complexRepository.save(complexEntity.get());
			return new ComplexDto(complexDb);
		}
		throw new EntityNotFoundException();
	}


	@Override
	public Response delete(Long id) {
		Optional<Complex> complex = complexRepository.findById(id);
		if (complex.isPresent()) {
			complexRepository.remove(complex.get());
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
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