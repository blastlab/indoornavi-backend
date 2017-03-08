package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ComplexBean implements ComplexFacade {

	private final ComplexRepository complexRepository;

	private final PermissionBean permissionBean;

	private final AuthorizationBean authorizationBean;

	@Inject
	public ComplexBean(ComplexRepository complexRepository, PermissionBean permissionBean, AuthorizationBean authorizationBean) {
		this.complexRepository = complexRepository;
		this.permissionBean = permissionBean;
		this.authorizationBean = authorizationBean;
	}

	@Override
	public Response test() {
		return Response.ok().build();
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
		try {
			permissionBean.checkPermission(id, Permission.DELETE);
		} catch (PermissionException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
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


	@Override
	public List<ComplexDto> findForCurrentUser() {
		Person currentUser = authorizationBean.getCurrentUser();
		List<ComplexDto> complexes = new ArrayList<>();
		complexRepository.findAllByPerson(currentUser.getId()).forEach(complex -> complexes.add(new ComplexDto(complex)));
		return complexes;
	}

//	public List<ComplexDto> findByPerson(Long personId) {
//		if (personId != null) {
//			if (!personId.equals(authorizationBean.getCurrentUser().getId())) {
//				throw new PermissionException();
//			}
//			Person person = personRepository.findBy(personId);
//			if (person != null) {
//				List<ComplexDto> complexes = new ArrayList<>();
//				new HashSet<>(complexRepository.findAllByPerson(personId)).forEach((complexEntity -> {
//					List<String> permissions = new ArrayList<>();
//					complexEntity.getACL_complexes().forEach((aclComplex) -> {
//						if (Objects.equals(aclComplex.getPerson().getId(), personId)) {
//							permissions.add(aclComplex.getPermission().getName());
//						}
//					});
//					complexes.add(new ComplexDto(complexEntity, permissions));
//				}));
//				return complexes;
//			}
//		}
//		throw new EntityNotFoundException();
//	}


	//	public ComplexDto findByBuilding(Long id) {
//		if (id != null) {
//			Complex complexEntity = complexRepository.findByBuildingId(id);
//			if (complexEntity != null) {
//				List<String> permissions = checkPermissions(complexEntity.getId());
//				return new ComplexDto(complexEntity, permissions);
//			}
//		}
//		throw new EntityNotFoundException();
//	}

	//	public ComplexDto findByFloor(Long id) {
//		if (id != null) {
//			Complex complexEntity = complexRepository.findByFloorId(id);
//			if (complexEntity != null) {
//				List<String> permissions = checkPermissions(complexEntity.getId());
//				return new ComplexDto(complexEntity, permissions);
//			}
//		}
//		throw new EntityNotFoundException();
//	}


//	private void checkForDuplicateComplex(String name, Predicate<Complex> additionalCondition) {
//		Complex complexEntity = complexRepository.findOptionalByName(name);
//		if (complexEntity != null && additionalCondition.test(complexEntity)) {
//			throw new EntityExistsException();
//		}
//	}
//
//	private List<String> checkPermissions(Long complexId) {
//		List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complexId);
//		if (!permissions.contains(Permission.READ)) {
//			throw new PermissionException();
//		}
//		return permissions;
//	}
}