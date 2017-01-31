package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.ACL_ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.ComplexRepository;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.ACL_Complex;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Michał Koszałka
 */
@Path("/complex")
@Api("/complex")
@Stateless
public class ComplexFacade {

    @EJB
    private ComplexBean complexBean;

    @Inject
    private ComplexRepository complexRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private ACL_ComplexRepository aclComplexRepository;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create complex", response = Complex.class)
    public Complex create(@ApiParam(value = "complex", required = true) Complex complex) {
        Complex c = complexRepository.findOptionalByName(complex.getName());
        if (c != null) {
            throw new EntityExistsException();
        }
        complexRepository.saveAndFlush(complex);
        List<ACL_Complex> aclComplexes = new ArrayList<>();
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.READ)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.CREATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.UPDATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.DELETE)));
        for (ACL_Complex aclComplex : aclComplexes) {
            aclComplexRepository.save(aclComplex);
        }
        complex.setPermissions(permissionBean.getPermissions(authorizationBean.getCurrentUser().getId(), complex.getId()));
        return complex;
    }

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    public Complex find(@ApiParam(value = "id", required = true) @PathParam("id") Long id) {
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

    @GET
    @Path("/building/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by building id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    public Complex findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long id) {
        if (id != null) {
            Complex complex = complexRepository.findByBuildingId(id);
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

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by floor id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    public Complex findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id) {
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

    @GET
    @Path("/complete/{id: \\d+}")
    @JsonView(View.External.class)
    @ApiOperation(value = "find complete complex by id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    public Complex findComplete(@ApiParam(value = "id", required = true) @PathParam("id") Long id) {
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

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete complex by id", response = Response.class)
    @ApiResponses({
        @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    public Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id) {
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

    @GET
    @Path("/person/{id: \\d+}")
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "find complexes by person id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "person id empty, person or complex doesn't exist")
    })
    public List<Complex> findByPerson(@ApiParam(value = "personId", required = true) @PathParam("id") Long personId) {
        if (personId != null) {
            if (!personId.equals(authorizationBean.getCurrentUser().getId())) {
                throw new PermissionException();
            }
            Person person = personRepository.findBy(personId);
            if (person != null) {
                return complexBean.findAllByPerson(personId);
            }
        }
        throw new EntityNotFoundException();
    }

    @PUT
    @ApiOperation(value = "delete complex by id", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    public Complex update(@ApiParam(value = "complex", required = true) Complex complex) {
        Complex c = complexRepository.findOptionalByName(complex.getName());
        if (c != null && !Objects.equals(c.getId(), complex.getId())) {
            throw new EntityExistsException();
        }
        complexRepository.save(complex);
        return complex;
    }

}
