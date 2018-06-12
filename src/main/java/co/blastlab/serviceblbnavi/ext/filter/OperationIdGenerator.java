package co.blastlab.serviceblbnavi.ext.filter;

import co.blastlab.serviceblbnavi.utils.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@GenerateOperationID
@Provider
public class OperationIdGenerator implements ContainerResponseFilter {

	@Inject
	private Logger logger;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		String id = UUID.randomUUID().toString();
		responseContext.getHeaders().add("UUID", id);
		logger.setId(id);
	}
}
