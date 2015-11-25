package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.ACL_ComplexBean;
import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.domain.ACL_Complex;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.views.View;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Michał Koszałka
 */
@Path("/complex")
@Api("/complex")
public class ComplexFacade {

    @EJB
    private ComplexBean complexBean;

    @EJB
    private PersonBean personBean;

    @EJB
    private ACL_ComplexBean aclComplexBean;

    @EJB
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @POST
    @ApiOperation(value = "create complex", response = Complex.class)
    public Complex create(@ApiParam(value = "complex", required = true) Complex complex) {
        complexBean.create(complex);
        List<ACL_Complex> aclComplexes = new ArrayList<>();
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.READ)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.CREATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.UPDATE)));
        aclComplexes.add(new ACL_Complex(authorizationBean.getCurrentUser(), complex, permissionBean.findByName(Permission.DELETE)));
        aclComplexBean.create(aclComplexes);
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
            Complex complex = complexBean.find(id);
            if (complex != null) {
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
            Complex complex = complexBean.find(id);
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
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(), id, Permission.DELETE);
            Complex complex = complexBean.find(id);
            if (complex != null) {
                complexBean.delete(complex);
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
            Person person = personBean.find(personId);
            if (person != null) {
                return complexBean.findAllByPerson(personId);
            }
        }
        throw new EntityNotFoundException();
    }

}
