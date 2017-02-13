package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.ACL_ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.PermissionRepository;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.ACL_Complex;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
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

    public Complex create(Complex complex) {
        Complex c = complexRepository.findOptionalByName(complex.getName());
        if (c != null) {
            throw new EntityExistsException();
        }
        complexRepository.saveAndFlush(complex);
        List<ACL_Complex> aclComplexes = new ArrayList<>();
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionRepository.findByName(Permission.READ)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionRepository.findByName(Permission.CREATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionRepository.findByName(Permission.UPDATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionRepository.findByName(Permission.DELETE)));
        for (ACL_Complex aclComplex : aclComplexes) {
            aclComplexRepository.save(aclComplex);
        }
        complex.setPermissions(permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complex.getId()));
        return complex;
    }


    public Complex find(Long id) {
        if (id != null) {
            List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), id);
            if (!permissions.contains(Permission.READ)) {
                throw new PermissionException();
            }
            Complex complex = complexRepository.findBy(id);
            if (complex != null) {
                complex.setPermissions(permissions);
                return complex;
            }
        }
        throw new EntityNotFoundException();
    }


    public Complex findByBuilding(Long id) {
        if (id != null) {
            Complex complex = complexRepository.findByBuildingId(id);
            Complex complex1 = new Complex();
            complex1.setName("siema");
            complexRepository.save(complex1);
            if (complex != null) {
                List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complex.getId());
                if (!permissions.contains(Permission.READ)) {
                    throw new PermissionException();
                }
                complex.setPermissions(permissions);
                return complex;
            }
        }
        throw new EntityNotFoundException();
    }

    public Complex findByFloor(Long id) {
        if (id != null) {
            Complex complex = complexRepository.findByFloorId(id);
            if (complex != null) {
                List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complex.getId());
                if (!permissions.contains(Permission.READ)) {
                    throw new PermissionException();
                }
                complex.setPermissions(permissions);
                return complex;
            }
        }
        throw new EntityNotFoundException();
    }


    public Complex findComplete(Long id) {
        if (id != null) {
            List<String> permissions = permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), id);
            if (!permissions.contains(Permission.READ)) {
                throw new PermissionException();
            }
            Complex complex = complexRepository.findBy(id);
            if (complex != null) {
                complex.setPermissions(permissions);
                return complex;
            }
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        if (id != null) {
            try {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), id, Permission.DELETE);
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


    public List<Complex> findByPerson(Long personId) {
        if (personId != null) {
            if (!personId.equals(authorizationBean.getCurrentUser().getId())) {
                throw new PermissionException();
            }
            Person person = personRepository.findBy(personId);
            if (person != null) {
                return findAllByPerson(personId);
            }
        }
        throw new EntityNotFoundException();
    }


    public Complex update(Complex complex) {
        Complex c = complexRepository.findOptionalByName(complex.getName());
        if (c != null && !Objects.equals(c.getId(), complex.getId())) {
            throw new EntityExistsException();
        }
        complexRepository.save(complex);
        return complex;
    }

    public List<Complex> findAllByPerson(Long personId) {
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