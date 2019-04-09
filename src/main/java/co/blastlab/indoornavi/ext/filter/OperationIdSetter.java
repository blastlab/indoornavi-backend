package co.blastlab.indoornavi.ext.filter;

import co.blastlab.indoornavi.utils.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@SetOperationId
@Provider
public class OperationIdSetter implements ContainerResponseFilter {

	@Inject
	private Logger logger;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		responseContext.getHeaders().add("UUID", logger.getId());
	}
}
