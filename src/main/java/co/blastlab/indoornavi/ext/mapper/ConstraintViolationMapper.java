package co.blastlab.indoornavi.ext.mapper;

import co.blastlab.indoornavi.ext.mapper.accessory.MessagePack;
import co.blastlab.indoornavi.ext.mapper.content.DbConstraintViolationContent;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static co.blastlab.indoornavi.ext.mapper.accessory.MessageConstraintSeacher.retrieveMessageByConstraintName;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

	@Override
	public Response toResponse(EJBTransactionRolledbackException exception) {

		MessagePack messagePack = retrieveMessageByConstraintName(exception);

		return Response.status(Response.Status.BAD_REQUEST)
			.entity(new DbConstraintViolationContent(messagePack)).build();
	}
}
