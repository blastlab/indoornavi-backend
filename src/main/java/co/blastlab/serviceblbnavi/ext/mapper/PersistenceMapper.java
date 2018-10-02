package co.blastlab.serviceblbnavi.ext.mapper;

import co.blastlab.serviceblbnavi.ext.mapper.accessory.MessagePack;
import co.blastlab.serviceblbnavi.ext.mapper.content.DbConstraintViolationContent;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static co.blastlab.serviceblbnavi.ext.mapper.accessory.MessageConstraintSeacher.retrieveMessageByConstraintName;

@Provider
public class PersistenceMapper implements ExceptionMapper<PersistenceException> {

	@Override
	public Response toResponse(PersistenceException exception) {
		Response.Status status;

		if (exception instanceof EntityNotFoundException) {
			status = Response.Status.NOT_FOUND;
		} else if (exception instanceof EntityExistsException) {
			status = Response.Status.CONFLICT;
		}  else if (exception.getCause() instanceof ConstraintViolationException){
			status = Response.Status.BAD_REQUEST;
			MessagePack messagePack = retrieveMessageByConstraintName(exception);

			return Response.status(status).entity(new DbConstraintViolationContent(messagePack)).build();
		} else {
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}

		return Response.status(status).entity("").build();
	}
}
