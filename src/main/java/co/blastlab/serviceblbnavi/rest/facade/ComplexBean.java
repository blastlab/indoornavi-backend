package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.function.Predicate;

@Stateless
public class ComplexBean implements ComplexFacade {

	@Inject
	private ComplexRepository complexRepository;

	public ComplexDto create(ComplexDto complex) {
		checkForDuplicateComplex(complex.getName(), (c) -> true);
		Complex complexEntity = new Complex();
		complexEntity.setName(complex.getName());
		complexEntity = complexRepository.saveAndFlush(complexEntity);
		return new ComplexDto(complexEntity);
	}

	public ComplexDto find(Long id) {
		if (id != null) {
			Complex complexEntity = complexRepository.findBy(id);
			if (complexEntity != null) {
				return new ComplexDto(complexEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public ComplexDto.WithBuildings findComplete(Long id) {
		if (id != null) {
			Complex complexEntity = complexRepository.findBy(id);
			if (complexEntity != null) {
				return new ComplexDto.WithBuildings(complexEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public ComplexDto findByBuilding(Long id) {
		if (id != null) {
			Complex complexEntity = complexRepository.findByBuildingId(id);
			if (complexEntity != null) {
				return new ComplexDto(complexEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public ComplexDto findByFloor(Long id) {
		if (id != null) {
			Complex complexEntity = complexRepository.findByFloorId(id);
			if (complexEntity != null) {
				return new ComplexDto(complexEntity);
			}
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		if (id != null) {
			Complex complex = complexRepository.findBy(id);
			if (complex != null) {
				complexRepository.remove(complex);
				return Response.ok().build();
			}
		}
		throw new EntityNotFoundException();
	}

	public ComplexDto update(ComplexDto complex) {
		checkForDuplicateComplex(complex.getName(), (c) -> !Objects.equals(c.getId(), complex.getId()));
		Complex complexEntity = complexRepository.findBy(complex.getId());
		if (complexEntity != null){
			complexEntity.setName(complex.getName());
			complexEntity = complexRepository.save(complexEntity);
			return new ComplexDto(complexEntity);
		}
		throw new EntityNotFoundException();
	}

	private void checkForDuplicateComplex(String name, Predicate<Complex> additionalCondition) {
		Complex complexEntity = complexRepository.findOptionalByName(name);
		if (complexEntity != null && additionalCondition.test(complexEntity)) {
			throw new EntityExistsException();
		}
	}
}