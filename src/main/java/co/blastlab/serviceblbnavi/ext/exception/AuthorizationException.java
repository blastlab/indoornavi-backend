package co.blastlab.serviceblbnavi.ext.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AuthorizationException extends WebApplicationException {

	public AuthorizationException() {
		super(Response.Status.UNAUTHORIZED);
	}
}
