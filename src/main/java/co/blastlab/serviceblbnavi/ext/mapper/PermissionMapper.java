package co.blastlab.serviceblbnavi.ext.mapper;

import co.blastlab.serviceblbnavi.ext.exception.PermissionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PermissionMapper implements ExceptionMapper<PermissionException> {

	@Override
	public Response toResponse(PermissionException exception) {
		return Response.status(Response.Status.UNAUTHORIZED)
			.type(MediaType.APPLICATION_JSON)
			.build();
	}
}
