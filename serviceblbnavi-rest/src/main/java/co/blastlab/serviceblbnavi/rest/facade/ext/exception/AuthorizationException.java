package co.blastlab.serviceblbnavi.rest.facade.ext.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Michal Koszalka
 */
public class AuthorizationException extends WebApplicationException {

	public AuthorizationException() {
		super(Response.Status.UNAUTHORIZED);
	}

}
