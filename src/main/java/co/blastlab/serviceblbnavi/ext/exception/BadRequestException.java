package co.blastlab.serviceblbnavi.ext.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

	public static final String ACCOUNT_NOT_ACTIVE = "account_not_active";
	public static final String ACCOUNT_IS_ACTIVE = "account_is_active";
	public static final String TO_RECENT_ACTION = "too_recent_action";
	public static final String INVALID_PASSWORD = "invalid_password";
	public static final String FIELD_NOT_ACTIVE = "field_not_active";

	public BadRequestException() {
		super(Response.Status.BAD_REQUEST);
	}

	public BadRequestException(String message) {
		super(message, Response.Status.BAD_REQUEST);
	}
}
