package co.blastlab.serviceblbnavi.rest.facade.ext.mapper;

import co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content.MessageErrorResponseContent;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Michal Koszalka
 */
@Provider
public class WebApplicationMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException exception) {
		// don't add message to response if it's default HTTP status description
		String defaultMessage = new WebApplicationException(exception.getResponse().getStatus()).getMessage();
		Object content = defaultMessage.equals(exception.getMessage()) ? "" : new MessageErrorResponseContent(exception);

		return Response.status(exception.getResponse().getStatus()).entity(content).build();
	}
}
