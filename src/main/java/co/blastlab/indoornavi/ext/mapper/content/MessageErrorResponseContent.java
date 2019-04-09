package co.blastlab.indoornavi.ext.mapper.content;

import lombok.NoArgsConstructor;

import javax.ws.rs.WebApplicationException;

@NoArgsConstructor
public class MessageErrorResponseContent extends ErrorResponseContent {

	private String error;

	public MessageErrorResponseContent(WebApplicationException exception) {
		error = exception.getMessage();
	}

	@Override
	public String getError() {
		return error;
	}
}
