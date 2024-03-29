package co.blastlab.indoornavi.ext.mapper;

import co.blastlab.indoornavi.ext.mapper.content.ConstraintViolationListErrorResponseContent;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		return Response
			.status(Response.Status.BAD_REQUEST)
			.entity(new ConstraintViolationListErrorResponseContent(exception))
			.build();
	}
}
