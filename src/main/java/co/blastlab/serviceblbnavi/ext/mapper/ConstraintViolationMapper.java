package co.blastlab.serviceblbnavi.ext.mapper;

import co.blastlab.serviceblbnavi.ext.mapper.content.DbConstraintViolationContent;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static co.blastlab.serviceblbnavi.ext.mapper.accessory.MessageConstraintSeacher.retrieveMessageByConstraintName;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

	@Override
	public Response toResponse(EJBTransactionRolledbackException exception) {

		String message = retrieveMessageByConstraintName(exception);

		return Response.status(Response.Status.BAD_REQUEST)
			.entity(new DbConstraintViolationContent(message)).build();
	}
}