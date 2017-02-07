package co.blastlab.serviceblbnavi.rest.facade.ext.mapper;

import co.blastlab.serviceblbnavi.dao.exception.PermissionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Grzegorz Konupek
 */
@Provider
public class PermissionMapper implements ExceptionMapper<PermissionException> {

    @Override
    public Response toResponse(PermissionException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
