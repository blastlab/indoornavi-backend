package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.ACL_ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.PermissionRepository;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.ACL_Complex;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;


@Stateless
public class ComplexEJB implements ComplexFacade {

    @Inject
    private ComplexRepository complexRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private ACL_ComplexRepository aclComplexRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private PermissionRepository permissionRepository;

    @Inject
    private AuthorizationBean authorizationBean;

    public ComplexDto create(ComplexDto complex) {
        checkForDuplicateComplex(complex.getName(), (c) -> true);
        Complex complexEntity = new Complex();
        complexEntity.setName(complex.getName());
        complexEntity = complexRepository.saveAndFlush(complexEntity);
        aclComplexRepository.save(new ACL_Complex(authorizationBean.getCurrentUser(), complexEntity, permissionRepository.findByName(Permission.READ)));
        aclComplexRepository.save(new ACL_Complex(authorizationBean.getCurrentUser(), complexEntity, permissionRepository.findByName(Permission.CREATE)));
        aclComplexRepository.save(new ACL_Complex(authorizationBean.getCurrentUser(), complexEntity, permissionRepository.findByName(Permission.UPDATE)));
        aclComplexRepository.save(new ACL_Complex(authorizationBean.getCurrentUser(), complexEntity, permissionRepository.findByName(Permission.DELETE)));
        return new ComplexDto(complexEntity, permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complexEntity.getId()));
    }


    public ComplexDto find(Long id) {
        if (id != null) {
            List<String> permissions = checkPermissions(id);
            Complex complexEntity = complexRepository.findBy(id);
            if (complexEntity != null) {
                return new ComplexDto(complexEntity, permissions);
            }
        }
        throw new EntityNotFoundException();
    }


    public ComplexDto findComplete(Long id) {
        return find(id);
    }


    public ComplexDto findByBuilding(Long id) {
        if (id != null) {
            Complex complexEntity = complexRepository.findByBuildingId(id);
            if (complexEntity != null) {
                List<String> permissions = checkPermissions(complexEntity.getId());
                return new ComplexDto(complexEntity, permissions);
            }
        }
        throw new EntityNotFoundException();
    }


    public ComplexDto findByFloor(Long id) {
        if (id != null) {
            Complex complexEntity = complexRepository.findByFloorId(id);
            if (complexEntity != null) {
                List<String> permissions = checkPermissions(complexEntity.getId());
                return new ComplexDto(complexEntity, permissions);
            }
        }
        throw new EntityNotFoundException();
    }

    public Response delete(Long id) {
        if (id != null) {
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
        }
        throw new EntityNotFoundException();
    }


    public List<ComplexDto> findByPerson(Long personId) {
        if (personId != null) {
            if (!personId.equals(authorizationBean.getCurrentUser().getId())) {
                throw new PermissionException();
            }
            Person person = personRepository.findBy(personId);
            if (person != null) {
                List<ComplexDto> complexes = new ArrayList<>();
                new HashSet<>(complexRepository.findAllByPerson(personId)).forEach((complexEntity -> {
                    List<String> permissions = new ArrayList<>();
                    complexEntity.getACL_complexes().forEach((aclComplex) -> {
                        if (Objects.equals(aclComplex.getPerson().getId(), personId)) {
                            permissions.add(aclComplex.getPermission().getName());
                        }
                    });
                    complexes.add(new ComplexDto(complexEntity, permissions));
                }));
                return complexes;
            }
        }
        throw new EntityNotFoundException();
    }


    public ComplexDto update(ComplexDto complex) {
        checkForDuplicateComplex(complex.getName(), (c) -> !Objects.equals(c.getId(), complex.getId()));
        Complex complexEntity = complexRepository.findBy(complex.getId());
        complexEntity.setName(complex.getName());
        complexEntity = complexRepository.save(complexEntity);
        return new ComplexDto(complexEntity, permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complexEntity.getId()));
    }


    private void checkForDuplicateComplex(String name, Predicate<Complex> additionalCondition) {
        Complex complexEntity = complexRepository.findOptionalByName(name);
        if (complexEntity != null && additionalCondition.test(complexEntity)) {
            throw new EntityExistsException();
        }
    }


    private List<String> checkPermissions(Long complexId) {
        List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complexId);
        if (!permissions.contains(Permission.READ)) {
            throw new PermissionException();
        }
        return permissions;
    }


    private List<Complex> findAllByPerson(Long personId) {
        List<Complex> complexes = complexRepository.findAllByPerson(personId);
        Set<Complex> complexSet = new HashSet<>(complexes);
        complexes = new ArrayList<>(complexSet);

        complexes.forEach((complex) -> {
            List<String> permissions = new ArrayList<>();
            complex.getACL_complexes().stream().forEach((aclComplex) -> {
                if (Objects.equals(aclComplex.getPerson().getId(), personId)) {
                    permissions.add(aclComplex.getPermission().getName());
                }
            });
            complex.setPermissions(permissions);
        });

        return complexes;
    }
}