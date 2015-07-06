package co.blastlab.serviceblbnavi.rest.facade.ext.mapper;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Maciej Radzikowski
 */
@Provider
public class PersistenceMapper implements ExceptionMapper<PersistenceException> {

	@Override
	public Response toResponse(PersistenceException exception) {
		Response.Status status;
		if (exception instanceof EntityNotFoundException) {
			status = Response.Status.NOT_FOUND;
		} else if (exception instanceof EntityExistsException) {
			status = Response.Status.CONFLICT;
		} else {
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity("").build();
	}
}
