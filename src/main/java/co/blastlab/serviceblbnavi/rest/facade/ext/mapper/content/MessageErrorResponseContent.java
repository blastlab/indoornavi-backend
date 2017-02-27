package co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content;

import javax.ws.rs.WebApplicationException;

public class MessageErrorResponseContent extends ErrorResponseContent {

	private String error;

	public MessageErrorResponseContent() {
	}

	public MessageErrorResponseContent(WebApplicationException exception) {
		error = exception.getMessage();
	}

	@Override
	public String getError() {
		return error;
	}
}
