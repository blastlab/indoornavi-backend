package co.blastlab.serviceblbnavi.rest.facade.ext.filter;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.ejb.AuthorizationBean;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Filter that checks all requests and perform authentication.
 * @author Michal Koszalka
 */
@RequestScoped
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

	private static final String AUTH_TOKEN = "auth_token";

	@Inject
	private AuthorizationBean authorizationBean;

	@EJB
	private PersonBean personBean;

	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException {
		if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
			requestCtx.abortWith(Response.status(Response.Status.OK).build());
			return;
		}

		String path = requestCtx.getUriInfo().getAbsolutePath().toString();

		if (!path.endsWith("/person")) {
			String authToken = requestCtx.getHeaderString(AUTH_TOKEN);
			if (authToken == null) {
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}

			Person person = personBean.findByAuthToken(authToken);
			if (person == null) {
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}

			authorizationBean.setCurrentUser(person);
		}
	}
}
