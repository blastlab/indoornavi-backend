package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
		// TODO: we need to add current user as admin of this complex
		complexEntity = complexRepository.save(complexEntity);
		return new ComplexDto(complexEntity);
	}


	@Override
	public ComplexDto update(Long id, ComplexDto complex) {
		Complex complexEntity = complexRepository.findBy(complex.getId());
		if (complexEntity != null){
			complexEntity.setName(complex.getName());
			complexEntity = complexRepository.save(complexEntity);
			return new ComplexDto(complexEntity);
		}
		throw new EntityNotFoundException();
	}


	@Override
	public Response delete(Long id) {
		Complex complex = complexRepository.findBy(id);
		if (complex != null) {
			complexRepository.remove(complex);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}


	@Override
	public ComplexDto find(Long id) {
		Complex complexEntity = complexRepository.findBy(id);
		if (complexEntity != null) {
			return new ComplexDto(complexEntity);
		}
		throw new EntityNotFoundException();
	}


	@Override
	public ComplexDto.WithBuildings findWithBuildings(Long id) {
		Complex complexEntity = complexRepository.findBy(id);
		if (complexEntity != null) {
			return new ComplexDto.WithBuildings(complexEntity);
		}
		throw new EntityNotFoundException();

}